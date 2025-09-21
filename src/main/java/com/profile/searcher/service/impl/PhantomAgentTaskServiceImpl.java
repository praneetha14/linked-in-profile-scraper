package com.profile.searcher.service.impl;

import com.profile.searcher.entity.PhantomAgentTaskEntity;
import com.profile.searcher.model.request.LinkedInProfileSearchDTO;
import com.profile.searcher.repository.PhantomAgentTaskRepository;
import com.profile.searcher.service.PhantomAgentTaskService;
import com.profile.searcher.service.mapper.GenericModelMapper;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class PhantomAgentTaskServiceImpl implements PhantomAgentTaskService {

    private final GenericModelMapper modelMapper;

    private final PhantomAgentTaskRepository taskRepository;

    @Override
    public UUID createPhantomBulkConsentTask(String containerId, LinkedInProfileSearchDTO linkedInProfileSearchDTO) {
        PhantomAgentTaskEntity entity = modelMapper.map(containerId, linkedInProfileSearchDTO);
        return taskRepository.save(entity).getId();
    }

    @Override
    public Optional<PhantomAgentTaskEntity> findPhantomAgentTaskByTrackingId(UUID trackingId) {
        return taskRepository.findById(trackingId);
    }
}
