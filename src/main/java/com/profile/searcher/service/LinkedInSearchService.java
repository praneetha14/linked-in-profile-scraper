package com.profile.searcher.service;

import com.profile.searcher.model.request.LinkedInProfileSearchDTO;
import com.profile.searcher.model.response.AlumniVO;
import com.profile.searcher.model.response.SuccessResponseVO;

import java.util.List;
import java.util.UUID;

public interface LinkedInSearchService {

    SuccessResponseVO<Object> searchAlumniLinkedInProfiles(LinkedInProfileSearchDTO searchDTO);

    SuccessResponseVO<Object> fetchScrapedAlumniLinkedInProfiles(UUID trackingId);

    SuccessResponseVO<List<AlumniVO>> fetchAllAlumni(int page, int limit);
}
