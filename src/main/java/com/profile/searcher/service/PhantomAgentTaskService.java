package com.profile.searcher.service;

import com.profile.searcher.entity.PhantomAgentTaskEntity;
import com.profile.searcher.model.request.LinkedInProfileSearchDTO;

import java.util.Optional;
import java.util.UUID;

public interface PhantomAgentTaskService {

    UUID createPhantomBulkConsentTask(String containerId, LinkedInProfileSearchDTO linkedInProfileSearchDTO);

    Optional<PhantomAgentTaskEntity> findPhantomAgentTaskByTrackingId(UUID trackingId);
}
