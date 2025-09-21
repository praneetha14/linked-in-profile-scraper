package com.profile.searcher.service.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.profile.searcher.AbstractTest;
import com.profile.searcher.entity.PhantomAgentTaskEntity;
import com.profile.searcher.model.phantom.buster.LinkedInProfileExportAgentResponse;
import com.profile.searcher.model.phantom.buster.LinkedInProfileScrapResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Slf4j
public class PhantomAgentTaskProcessingJobTest extends AbstractTest {

    @Autowired
    private PhantomAgentTaskProcessingJob phantomAgentTaskProcessingJob;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void testPhantomAgentTaskProcessingJob() throws JsonProcessingException {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                eq(LinkedInProfileScrapResponse.class)))
                .thenReturn(new ResponseEntity<>(getLinkedInProfileScrapResponse(), HttpStatus.OK));
        PhantomAgentTaskEntity phantomAgentTaskEntity = createPhantomAgentTaskEntity();
        phantomAgentTaskRepository.save(phantomAgentTaskEntity);
        assertDoesNotThrow(() -> phantomAgentTaskProcessingJob.processPhantomAgentTask());
    }

    @Test
    void testPhantomAgentTaskProcessingJobWithExceptionFailure() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                eq(LinkedInProfileScrapResponse.class)))
                .thenThrow(new RuntimeException("message"));
        PhantomAgentTaskEntity phantomAgentTaskEntity = createPhantomAgentTaskEntity();
        phantomAgentTaskRepository.save(phantomAgentTaskEntity);
        assertDoesNotThrow(() -> phantomAgentTaskProcessingJob.processPhantomAgentTask());
    }

    @Test
    void testPhantomAgentTaskProcessingJobWithErrorInResponse() throws JsonProcessingException {
        LinkedInProfileScrapResponse linkedInProfileScrapResponse = getLinkedInProfileScrapResponse();
        linkedInProfileScrapResponse.setExitCode(1);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                eq(LinkedInProfileScrapResponse.class)))
                .thenReturn(new ResponseEntity<>(getLinkedInProfileScrapResponse(), HttpStatus.OK));
        PhantomAgentTaskEntity phantomAgentTaskEntity = createPhantomAgentTaskEntity();
        phantomAgentTaskRepository.save(phantomAgentTaskEntity);
        assertDoesNotThrow(() -> phantomAgentTaskProcessingJob.processPhantomAgentTask());
    }

    @Test
    void testPhantomAgentTaskProcessingJobWithLessRetry() throws JsonProcessingException {
        LinkedInProfileScrapResponse linkedInProfileScrapResponse = getLinkedInProfileScrapResponse();
        linkedInProfileScrapResponse.setExitCode(1);
        linkedInProfileScrapResponse.setStatus("progress");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                eq(LinkedInProfileScrapResponse.class)))
                .thenReturn(new ResponseEntity<>(linkedInProfileScrapResponse, HttpStatus.OK));
        PhantomAgentTaskEntity phantomAgentTaskEntity = createPhantomAgentTaskEntity();
        phantomAgentTaskRepository.save(phantomAgentTaskEntity);
        assertDoesNotThrow(() -> phantomAgentTaskProcessingJob.processPhantomAgentTask());
    }

    @Test
    void testPhantomAgentTaskProcessingJobWithMoreRetry() throws JsonProcessingException {
        LinkedInProfileScrapResponse linkedInProfileScrapResponse = getLinkedInProfileScrapResponse();
        linkedInProfileScrapResponse.setExitCode(1);
        linkedInProfileScrapResponse.setStatus("progress");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                eq(LinkedInProfileScrapResponse.class)))
                .thenReturn(new ResponseEntity<>(linkedInProfileScrapResponse, HttpStatus.OK));
        PhantomAgentTaskEntity phantomAgentTaskEntity = createPhantomAgentTaskEntity();
        phantomAgentTaskEntity.setRetryCount(1);
        phantomAgentTaskRepository.save(phantomAgentTaskEntity);
        assertDoesNotThrow(() -> phantomAgentTaskProcessingJob.processPhantomAgentTask());
    }

    @Test
    void testPhantomAgentTaskProcessingJobWithExistingData() throws JsonProcessingException {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                eq(LinkedInProfileScrapResponse.class)))
                .thenReturn(new ResponseEntity<>(getLinkedInProfileScrapResponse(), HttpStatus.OK));
        createTestData();
        PhantomAgentTaskEntity phantomAgentTaskEntity = createPhantomAgentTaskEntity();
        phantomAgentTaskRepository.save(phantomAgentTaskEntity);
        assertDoesNotThrow(() -> phantomAgentTaskProcessingJob.processPhantomAgentTask());
    }

    private LinkedInProfileScrapResponse getLinkedInProfileScrapResponse() throws JsonProcessingException {
        LinkedInProfileScrapResponse linkedInProfileScrapResponse = new LinkedInProfileScrapResponse();
        linkedInProfileScrapResponse.setId("xysgdwpf");
        linkedInProfileScrapResponse.setStatus("finished");
        linkedInProfileScrapResponse.setExitCode(0);
        linkedInProfileScrapResponse.setRetryNumber("0");
        linkedInProfileScrapResponse.setResultObject(getResponseObjects());
        return linkedInProfileScrapResponse;
    }

    private String getResponseObjects() throws JsonProcessingException {
        List<LinkedInProfileExportAgentResponse> objects = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            LinkedInProfileExportAgentResponse linkedInProfileExportAgentResponse = new LinkedInProfileExportAgentResponse();
            linkedInProfileExportAgentResponse.setCompany("company");
            linkedInProfileExportAgentResponse.setCompanyUrl("companyUrl");
            linkedInProfileExportAgentResponse.setFullName("fullName");
            linkedInProfileExportAgentResponse.setProfileUrl("profileUrl");
            linkedInProfileExportAgentResponse.setConnectionDegree("2+");
            linkedInProfileExportAgentResponse.setLocation("location");
            linkedInProfileExportAgentResponse.setAdditionalInfo("additionalInfo");
            linkedInProfileExportAgentResponse.setIndustry("industry");
            linkedInProfileExportAgentResponse.setJobTitle("software engineer");
            linkedInProfileExportAgentResponse.setSchool("University");
            linkedInProfileExportAgentResponse.setSchoolDateRange("2019-2023");
            objects.add(linkedInProfileExportAgentResponse);
        }
        return objectMapper.writeValueAsString(objects);
    }

}
