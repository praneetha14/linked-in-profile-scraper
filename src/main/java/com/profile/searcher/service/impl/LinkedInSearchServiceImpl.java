package com.profile.searcher.service.impl;

import com.profile.searcher.entity.PhantomAgentTaskEntity;
import com.profile.searcher.model.enums.PhantomAgentTaskStatus;
import com.profile.searcher.model.exceptions.LinkedInProfileSearcherException;
import com.profile.searcher.model.request.LinkedInProfileSearchDTO;
import com.profile.searcher.model.response.AlumniVO;
import com.profile.searcher.model.response.SuccessResponseVO;
import com.profile.searcher.repository.AlumniRepository;
import com.profile.searcher.service.LinkedInSearchService;
import com.profile.searcher.service.PhantomAgentTaskService;
import com.profile.searcher.service.PhantomBusterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class LinkedInSearchServiceImpl implements LinkedInSearchService {

    private final PhantomBusterService phantomBusterService;

    private final PhantomAgentTaskService phantomAgentTaskService;

    private final AlumniRepository alumniRepository;


    @Override
    public SuccessResponseVO<Object> searchAlumniLinkedInProfiles(LinkedInProfileSearchDTO linkedInProfileSearchDTO) {
        SuccessResponseVO<Object> successResponseVO = getAlumniData(linkedInProfileSearchDTO.getCurrentDesignation(),
                linkedInProfileSearchDTO.getUniversity(), linkedInProfileSearchDTO.getGraduationYear());
        List<AlumniVO> alumniVOList = (List<AlumniVO>) successResponseVO.getData();
        if (!alumniVOList.isEmpty()) {
            return successResponseVO;
        }
        return phantomBusterService.searchLinkedInProfiles(linkedInProfileSearchDTO);
    }

    @Override
    public SuccessResponseVO<Object> fetchScrapedAlumniLinkedInProfiles(UUID trackingId) {
        Optional<PhantomAgentTaskEntity> phantomAgentTask = phantomAgentTaskService
                .findPhantomAgentTaskByTrackingId(trackingId);
        if (phantomAgentTask.isEmpty()) {
            throw new LinkedInProfileSearcherException("Invalid trackingId");
        }
        PhantomAgentTaskEntity phantomAgentTaskEntity = phantomAgentTask.get();
        if (PhantomAgentTaskStatus.AGENT_LAUNCHED.equals(phantomAgentTaskEntity.getPhantomAgentTaskStatus())) {
            return SuccessResponseVO.of("LinkedIn profile scraping is still in progress", null);
        }
        if (PhantomAgentTaskStatus.TASK_FAILED.equals(phantomAgentTaskEntity.getPhantomAgentTaskStatus())) {
            throw new LinkedInProfileSearcherException("LinkedIn profile scraping failed");
        }
        return getAlumniData(phantomAgentTaskEntity.getCurrentDesignation(), phantomAgentTaskEntity.getUniversity(),
                phantomAgentTaskEntity.getPassedOutYear());
    }

    @Override
    public SuccessResponseVO<List<AlumniVO>> fetchAllAlumni(int page, int limit) {
        if (page < 0) {
            page = 0;
        }
        if (limit <= 0) {
            limit = 10;
        }
        Pageable pageable = PageRequest.of(page, limit);
        return SuccessResponseVO.of("Fetched all alumni successfully",
                alumniRepository.findAllAlumni(pageable).stream().toList());
    }

    private SuccessResponseVO<Object> getAlumniData(String currentDesignation, String university,
                                                    Integer graduationYear) {
        List<AlumniVO> alumniVOList;
        if (graduationYear != null) {
            alumniVOList = alumniRepository.findAllAlumni(currentDesignation.toLowerCase(), university, graduationYear);
        } else {
            alumniVOList = alumniRepository.findAllAlumniWithoutGraduationYear(currentDesignation.toLowerCase(), university);
        }
        return SuccessResponseVO.of("Fetched alumni data successfully", alumniVOList);
    }
}
