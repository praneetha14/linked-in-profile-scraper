package com.profile.searcher.service;

import com.profile.searcher.model.request.LinkedInProfileSearchDTO;
import com.profile.searcher.model.response.SuccessResponseVO;

import java.util.UUID;

public interface PhantomBusterService {

    SuccessResponseVO<Object> searchLinkedInProfiles(LinkedInProfileSearchDTO linkedInProfileSearchDTO);
}
