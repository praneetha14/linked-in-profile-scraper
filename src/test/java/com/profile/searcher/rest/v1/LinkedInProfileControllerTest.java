package com.profile.searcher.rest.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.profile.searcher.model.exceptions.LinkedInProfileSearcherException;
import com.profile.searcher.model.request.LinkedInProfileSearchDTO;
import com.profile.searcher.service.LinkedInSearchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {LinkedInProfileController.class})
@ExtendWith(SpringExtension.class)
public class LinkedInProfileControllerTest {

    private static final String LINKED_IN_PROFILE_PATH = "/api/v1/linked-in";

    private static final String SEARCH_URL = "/search";

    private static final String FETCH_SCRAPED_PROFILES_URL = "/fetch/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LinkedInSearchService linkedInSearchService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void searchLinkedInProfileSuccessTest() throws Exception {
        LinkedInProfileSearchDTO linkedInProfileSearchDTO = getLinkedInProfileSearchDTO();
        mockMvc.perform(
                post(LINKED_IN_PROFILE_PATH + SEARCH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(linkedInProfileSearchDTO))
        ).andExpect(status().isOk());
    }

    @Test
    void searchLinkedInProfileWithNullValueForUniversityFailureTest() throws Exception {
        LinkedInProfileSearchDTO linkedInProfileSearchDTO = getLinkedInProfileSearchDTO();
        linkedInProfileSearchDTO.setUniversity(null);
        mockMvc.perform(
                post(LINKED_IN_PROFILE_PATH + SEARCH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(linkedInProfileSearchDTO))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void searchLinkedInProfileWithNullValueForCurrentDesignationFailureTest() throws Exception {
        LinkedInProfileSearchDTO linkedInProfileSearchDTO = getLinkedInProfileSearchDTO();
        linkedInProfileSearchDTO.setCurrentDesignation(null);
        mockMvc.perform(
                post(LINKED_IN_PROFILE_PATH + SEARCH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(linkedInProfileSearchDTO))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void searchLinkedInProfileWithInternalExceptionFailureTest() throws Exception {
        LinkedInProfileSearchDTO linkedInProfileSearchDTO = getLinkedInProfileSearchDTO();
        when(linkedInSearchService.searchAlumniLinkedInProfiles(linkedInProfileSearchDTO))
                .thenThrow(new LinkedInProfileSearcherException("internal error"));
        mockMvc.perform(
                post(LINKED_IN_PROFILE_PATH + SEARCH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(linkedInProfileSearchDTO))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void fetchScrapedLinkedInProfilesSuccessTest() throws Exception {
        mockMvc.perform(
                get(LINKED_IN_PROFILE_PATH + FETCH_SCRAPED_PROFILES_URL + UUID.randomUUID())
        ).andExpect(status().isOk());
    }

    @Test
    void fetchScrapedLinkedInProfilesWithExceptionFailureTest() throws Exception {
        when(linkedInSearchService.fetchScrapedAlumniLinkedInProfiles(any(UUID.class)))
                .thenThrow(new RuntimeException("message"));
        mockMvc.perform(
                get(LINKED_IN_PROFILE_PATH + FETCH_SCRAPED_PROFILES_URL + UUID.randomUUID())
        ).andExpect(status().isBadRequest());
    }

    @Test
    void fetchAllLinkedInProfilesSuccessTest() throws Exception {
        mockMvc.perform(
                get(LINKED_IN_PROFILE_PATH + FETCH_SCRAPED_PROFILES_URL + "all")
                        .param("page", "0")
                        .param("limit", "10")
        ).andExpect(status().isOk());
    }

    private LinkedInProfileSearchDTO getLinkedInProfileSearchDTO() {
        LinkedInProfileSearchDTO linkedInProfileSearchDTO = new LinkedInProfileSearchDTO();
        linkedInProfileSearchDTO.setGraduationYear(2023);
        linkedInProfileSearchDTO.setUniversity("University");
        linkedInProfileSearchDTO.setCurrentDesignation("software engineer");
        return linkedInProfileSearchDTO;
    }

}
