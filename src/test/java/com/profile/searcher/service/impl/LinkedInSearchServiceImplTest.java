package com.profile.searcher.service.impl;

import com.profile.searcher.AbstractTest;
import com.profile.searcher.entity.PhantomAgentTaskEntity;
import com.profile.searcher.model.enums.PhantomAgentTaskStatus;
import com.profile.searcher.model.exceptions.LinkedInProfileSearcherException;
import com.profile.searcher.model.phantom.buster.PhantomLaunchResponse;
import com.profile.searcher.model.request.LinkedInProfileSearchDTO;
import com.profile.searcher.model.response.AlumniVO;
import com.profile.searcher.model.response.SuccessResponseVO;
import com.profile.searcher.service.LinkedInSearchService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Slf4j
public class LinkedInSearchServiceImplTest extends AbstractTest {

    private static final String PHANTOM_LAUNCH_SUCCESS = "Phantom to scrap linkedIn data launched successfully. " +
            "You can access your data using result api by passing given tracking id with in few minutes";
    private static final String FETCH_ALUMNI_DATA_SUCCESS = "Fetched alumni data successfully";
    private static final String SCRAPING_IN_PROGRESS = "LinkedIn profile scraping is still in progress";
    private static final String SCRAPING_FAILED = "LinkedIn profile scraping failed";
    private static final String INVALID_TRACKING_ID = "Invalid trackingId";

    @Autowired
    private LinkedInSearchService linkedInSearchService;


    @MockBean
    private RestTemplate restTemplate;

    @Test
    void searchLinkedInProfilesSuccessTest() {
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(PhantomLaunchResponse.class)))
                .thenReturn(getAgentLaunchResponse());
        LinkedInProfileSearchDTO linkedInProfileSearchDTO = getLinkedInProfileSearchDTO();
        SuccessResponseVO<Object> successResponseVO = linkedInSearchService
                .searchAlumniLinkedInProfiles(linkedInProfileSearchDTO);
        assertNotNull(successResponseVO, ASSERT_MESSAGE);
        assertEquals(PHANTOM_LAUNCH_SUCCESS, successResponseVO.getMessage(),
                ASSERT_MESSAGE);
    }

    @Test
    void searchLinkedInProfilesWithExceptionWhileTriggeringPhantomFailureTest() {
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(PhantomLaunchResponse.class)))
                .thenThrow(new RuntimeException("message"));
        LinkedInProfileSearchDTO linkedInProfileSearchDTO = getLinkedInProfileSearchDTO();
        Throwable exception = assertThrows(LinkedInProfileSearcherException.class, () -> linkedInSearchService
                .searchAlumniLinkedInProfiles(linkedInProfileSearchDTO));
        assertNotNull(exception, ASSERT_MESSAGE);
        assertEquals("message", exception.getMessage(), ASSERT_MESSAGE);
    }

    @Test
    void searchLinkedInProfilesExistingSuccessTest() {
        createTestData();
        SuccessResponseVO<Object> successResponseVO = linkedInSearchService
                .searchAlumniLinkedInProfiles(getLinkedInProfileSearchDTO());
        assertNotNull(successResponseVO, ASSERT_MESSAGE);
        assertEquals(FETCH_ALUMNI_DATA_SUCCESS, successResponseVO.getMessage(), ASSERT_MESSAGE);
    }

    @Test
    void getScrapedLinkedInProfilesSuccessTest() {
        PhantomAgentTaskEntity phantomAgentTaskEntity = createPhantomAgentTaskEntity();
        phantomAgentTaskEntity.setPhantomAgentTaskStatus(PhantomAgentTaskStatus.DATA_RECEIVED);
        phantomAgentTaskEntity = phantomAgentTaskRepository.save(phantomAgentTaskEntity);
        SuccessResponseVO<Object> successResponseVO = linkedInSearchService
                .fetchScrapedAlumniLinkedInProfiles(phantomAgentTaskEntity.getId());
        assertNotNull(successResponseVO, ASSERT_MESSAGE);
        assertEquals(FETCH_ALUMNI_DATA_SUCCESS, successResponseVO.getMessage(), ASSERT_MESSAGE);
    }

    @Test
    void getScrapedLinkedInProfilesWithProgressStatusSuccessTest() {
        PhantomAgentTaskEntity phantomAgentTaskEntity = createPhantomAgentTaskEntity();
        phantomAgentTaskEntity = phantomAgentTaskRepository.save(phantomAgentTaskEntity);
        SuccessResponseVO<Object> successResponseVO = linkedInSearchService
                .fetchScrapedAlumniLinkedInProfiles(phantomAgentTaskEntity.getId());
        assertNotNull(successResponseVO, ASSERT_MESSAGE);
        assertEquals(SCRAPING_IN_PROGRESS, successResponseVO.getMessage(), ASSERT_MESSAGE);
    }

    @Test
    void getScrapedLinkedInProfileWithOutGraduationYearSuccessTest() {
        PhantomAgentTaskEntity phantomAgentTaskEntity = createPhantomAgentTaskEntity();
        phantomAgentTaskEntity.setPassedOutYear(null);
        phantomAgentTaskEntity.setPhantomAgentTaskStatus(PhantomAgentTaskStatus.DATA_RECEIVED);
        phantomAgentTaskEntity = phantomAgentTaskRepository.save(phantomAgentTaskEntity);
        SuccessResponseVO<Object> successResponseVO = linkedInSearchService
                .fetchScrapedAlumniLinkedInProfiles(phantomAgentTaskEntity.getId());
        assertNotNull(successResponseVO, ASSERT_MESSAGE);
        assertEquals(FETCH_ALUMNI_DATA_SUCCESS, successResponseVO.getMessage(), ASSERT_MESSAGE);
    }

    @Test
    void getScrapedLinkedInProfilesWithFailedStatusFailureTest() {
        PhantomAgentTaskEntity phantomAgentTaskEntity = createPhantomAgentTaskEntity();
        phantomAgentTaskEntity.setPhantomAgentTaskStatus(PhantomAgentTaskStatus.TASK_FAILED);
        phantomAgentTaskEntity = phantomAgentTaskRepository.save(phantomAgentTaskEntity);
        PhantomAgentTaskEntity finalPhantomAgentTaskEntity = phantomAgentTaskEntity;
        Throwable exception = assertThrows(LinkedInProfileSearcherException.class, () -> linkedInSearchService
                .fetchScrapedAlumniLinkedInProfiles(finalPhantomAgentTaskEntity.getId()));
        assertNotNull(exception, ASSERT_MESSAGE);
        assertEquals(SCRAPING_FAILED, exception.getMessage(), ASSERT_MESSAGE);
    }

    @Test
    void getScrapedLinkedInProfilesWithInvalidTrackingIdFailureTest() {
        Throwable exception = assertThrows(LinkedInProfileSearcherException.class,
                () -> linkedInSearchService.fetchScrapedAlumniLinkedInProfiles(UUID.randomUUID()));
        assertNotNull(exception, ASSERT_MESSAGE);
        assertEquals(INVALID_TRACKING_ID, exception.getMessage(), ASSERT_MESSAGE);
    }

    @Test
    void getAllAlumniSuccessTest() {
        createTestData();
        SuccessResponseVO<List<AlumniVO>> successResponseVO = linkedInSearchService.fetchAllAlumni(0, 10);
        assertNotNull(successResponseVO, ASSERT_MESSAGE);
        assertEquals(10, successResponseVO.getData().size(), ASSERT_MESSAGE);
    }

    @Test
    void getAllAlumniWithNegativePageAndLimitSuccessTest() {
        createTestData();
        SuccessResponseVO<List<AlumniVO>> successResponseVO = linkedInSearchService.fetchAllAlumni(-1, -1);
        assertNotNull(successResponseVO, ASSERT_MESSAGE);
        assertEquals(10, successResponseVO.getData().size(), ASSERT_MESSAGE);
    }

    @Test
    void getAllAlumniWithZeroLimitSuccessTest() {
        createTestData();
        SuccessResponseVO<List<AlumniVO>> successResponseVO = linkedInSearchService.fetchAllAlumni(0, 0);
        assertNotNull(successResponseVO, ASSERT_MESSAGE);
        assertEquals(10, successResponseVO.getData().size(), ASSERT_MESSAGE);
    }


    private PhantomLaunchResponse getAgentLaunchResponse() {
        PhantomLaunchResponse launchResponse = new PhantomLaunchResponse();
        launchResponse.setContainerId("containerId");
        return launchResponse;
    }

    private LinkedInProfileSearchDTO getLinkedInProfileSearchDTO() {
        LinkedInProfileSearchDTO linkedInProfileSearchDTO = new LinkedInProfileSearchDTO();
        linkedInProfileSearchDTO.setUniversity("University");
        linkedInProfileSearchDTO.setCurrentDesignation("software engineer");
        linkedInProfileSearchDTO.setGraduationYear(2024);
        return linkedInProfileSearchDTO;
    }
}
