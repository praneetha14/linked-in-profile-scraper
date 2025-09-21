package com.profile.searcher.service.impl;

import com.profile.searcher.model.phantom.buster.PhantomLaunchResponse;
import com.profile.searcher.model.request.LinkedInProfileSearchDTO;
import com.profile.searcher.model.response.SuccessResponseVO;
import com.profile.searcher.service.PhantomAgentTaskService;
import com.profile.searcher.service.PhantomBusterService;
import com.profile.searcher.service.client.PhantomBusterClient;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class PhantomBusterServiceImpl implements PhantomBusterService {

    private final PhantomBusterClient phantomBusterClient;

    private final PhantomAgentTaskService phantomAgentTaskService;

    @Override
    public SuccessResponseVO<Object> searchLinkedInProfiles(LinkedInProfileSearchDTO linkedInProfileSearchDTO) {
        PhantomLaunchResponse phantomLaunchResponse = phantomBusterClient
                .triggerPhantomBusterSearch(linkedInProfileSearchDTO.getCurrentDesignation(),
                        linkedInProfileSearchDTO.getUniversity());
        UUID trackingId = phantomAgentTaskService.createPhantomBulkConsentTask(phantomLaunchResponse.getContainerId(),
                linkedInProfileSearchDTO);
        return SuccessResponseVO.of("Phantom to scrap linkedIn data launched successfully. " +
                "You can access your data using result api by passing given tracking id with in few minutes", trackingId);
    }
}
