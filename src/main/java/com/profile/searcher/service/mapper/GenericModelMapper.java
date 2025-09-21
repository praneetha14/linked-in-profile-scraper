package com.profile.searcher.service.mapper;

import com.profile.searcher.entity.AlumniEntity;
import com.profile.searcher.entity.PhantomAgentTaskEntity;
import com.profile.searcher.entity.UniversityEntity;
import com.profile.searcher.model.enums.PhantomAgentTaskStatus;
import com.profile.searcher.model.phantom.buster.LinkedInProfileExportAgentResponse;
import com.profile.searcher.model.request.LinkedInProfileSearchDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring",
        imports = {PhantomAgentTaskStatus.class}
)
public interface GenericModelMapper {

    @Mapping(target = "containerId", source = "containerId")
    @Mapping(target = "university", source = "dto.university")
    @Mapping(target = "currentDesignation", expression = "java(dto.getCurrentDesignation().toLowerCase())")
    @Mapping(target = "passedOutYear", source = "dto.graduationYear")
    @Mapping(target = "phantomAgentTaskStatus", expression = "java(PhantomAgentTaskStatus.AGENT_LAUNCHED)")
    PhantomAgentTaskEntity map(String containerId, LinkedInProfileSearchDTO dto);

    @Mapping(target = "title", expression = "java(agentResponse.getJobTitle() != null "
            + "? agentResponse.getJobTitle().toLowerCase(): agentResponse.getJobTitle())")
    @Mapping(target = "profileHeadLine", source = "agentResponse.additionalInfo")
    @Mapping(target = "university", source = "university")
    @Mapping(target = "passedOutYear", expression = "java(getPassedOutDate(agentResponse, university.getName()))")
    AlumniEntity map(LinkedInProfileExportAgentResponse agentResponse, UniversityEntity university);

    default Integer getPassedOutDate(LinkedInProfileExportAgentResponse response, String university) {
        if (university.equalsIgnoreCase(response.getSchool()) && StringUtils.hasText(response.getSchoolDateRange())) {
            String passedOutYear = Arrays.stream(response.getSchoolDateRange().split("-")).toList().get(1);
            return getYear(passedOutYear);
        } else {
            if (StringUtils.hasText(response.getSchoolDateRange2())) {
                String passedOutYear = Arrays.stream(response.getSchoolDateRange2().split("-")).toList().get(0);
                return getYear(passedOutYear);
            }
        }
        return null;
    }

    private Integer getYear(String input) {
        for (int i = 0; i <= input.length() - 4; i++) {
            if (Character.isDigit(input.charAt(i)) &&
                    Character.isDigit(input.charAt(i + 1)) &&
                    Character.isDigit(input.charAt(i + 2)) &&
                    Character.isDigit(input.charAt(i + 3))) {
                return Integer.parseInt(input.substring(i, i + 4));
            }
        }
        return null;
    }
}
