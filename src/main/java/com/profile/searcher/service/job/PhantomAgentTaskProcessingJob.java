package com.profile.searcher.service.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.profile.searcher.entity.AlumniEntity;
import com.profile.searcher.entity.PhantomAgentTaskEntity;
import com.profile.searcher.entity.UniversityEntity;
import com.profile.searcher.model.enums.PhantomAgentTaskStatus;
import com.profile.searcher.model.phantom.buster.LinkedInProfileExportAgentResponse;
import com.profile.searcher.model.phantom.buster.LinkedInProfileScrapResponse;
import com.profile.searcher.repository.PhantomAgentTaskRepository;
import com.profile.searcher.repository.UniversityRepository;
import com.profile.searcher.service.client.PhantomBusterClient;
import com.profile.searcher.service.mapper.GenericModelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
public class PhantomAgentTaskProcessingJob {

    private final PhantomAgentTaskRepository phantomAgentTaskRepository;
    private final PhantomBusterClient phantomBusterClient;
    private final ObjectMapper objectMapper;
    private final GenericModelMapper genericModelMapper;
    private final UniversityRepository universityRepository;

    @Scheduled(fixedDelay = 2, timeUnit = TimeUnit.MINUTES)
    public void processPhantomAgentTask() {
        List<PhantomAgentTaskEntity> phantomAgentTaskEntities = phantomAgentTaskRepository
                .findAllByPhantomAgentTaskStatus(PhantomAgentTaskStatus.AGENT_LAUNCHED);
        phantomAgentTaskEntities.forEach(phantomAgentTaskEntity -> {
            LinkedInProfileScrapResponse response;
            try {
                response = phantomBusterClient
                        .getContainerOutput(phantomAgentTaskEntity.getContainerId());
            } catch (Exception e) {
                log.error("PhantomAgentProcessing job failed for tracking id " + phantomAgentTaskEntity.getId(), e);
                phantomAgentTaskEntity.setPhantomAgentTaskStatus(PhantomAgentTaskStatus.TASK_FAILED);
                phantomAgentTaskRepository.save(phantomAgentTaskEntity);
                return;
            }
            if ("finished".equals(response.getStatus()) && response.getExitCode() == 0) {
                List<LinkedInProfileExportAgentResponse> agentResponse;
                try {
                    agentResponse = objectMapper.readValue(response.getResultObject(),
                            new TypeReference<>() {
                            });
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    phantomAgentTaskEntity.setPhantomAgentTaskStatus(PhantomAgentTaskStatus.TASK_FAILED);
                    phantomAgentTaskRepository.save(phantomAgentTaskEntity);
                    return;
                }
                UniversityEntity university = getUniversityEntity(phantomAgentTaskEntity.getUniversity());
                List<AlumniEntity> alumniEntities = createAlumni(university, agentResponse);
                if (!alumniEntities.isEmpty()) {
                    university.setAlumniEntities(alumniEntities);
                    universityRepository.save(university);
                }
                phantomAgentTaskEntity.setPhantomAgentTaskStatus(PhantomAgentTaskStatus.DATA_RECEIVED);
            } else {
                if ("finished".equals(response.getStatus()) && response.getExitCode() == 1) {
                    phantomAgentTaskEntity.setPhantomAgentTaskStatus(PhantomAgentTaskStatus.TASK_FAILED);
                } else {
                    if (phantomAgentTaskEntity.getRetryCount() < 1) {
                        phantomAgentTaskEntity.setRetryCount(phantomAgentTaskEntity.getRetryCount() + 1);
                    } else {
                        phantomAgentTaskEntity.setPhantomAgentTaskStatus(PhantomAgentTaskStatus.TASK_FAILED);
                    }
                }
            }
        });
        phantomAgentTaskRepository.saveAll(phantomAgentTaskEntities);
    }

    private UniversityEntity getUniversityEntity(String university) {
        UniversityEntity universityEntity = universityRepository.findByName(university);
        if (universityEntity != null) {
            return universityEntity;
        }
        UniversityEntity universityEntityNew = new UniversityEntity();
        universityEntityNew.setName(university);
        universityEntityNew.setAlumniEntities(List.of());
        return universityEntityNew;
    }

    private List<AlumniEntity> createAlumni(UniversityEntity university,
                                            List<LinkedInProfileExportAgentResponse> agentResponse) {
        List<AlumniEntity> alumniEntities = new ArrayList<>();
        agentResponse.forEach(agentResponseVO -> {
            boolean alumniAlreadyExist = university.getAlumniEntities().stream().anyMatch(alumniEntity -> alumniEntity.getProfileUrl()
                    .equals(agentResponseVO.getProfileUrl()));
            if (!alumniAlreadyExist) {
                alumniEntities.add(genericModelMapper.map(agentResponseVO, university));
            }
        });
        return alumniEntities;
    }
}
