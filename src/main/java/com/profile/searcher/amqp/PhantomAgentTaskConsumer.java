package com.profile.searcher.amqp;

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
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RabbitListener(queues = {"phantom-agent-task-queue"})
@RequiredArgsConstructor
@Slf4j
public class PhantomAgentTaskConsumer {

    private final PhantomAgentTaskRepository phantomAgentTaskRepository;
    private final PhantomBusterClient phantomBusterClient;
    private final ObjectMapper objectMapper;
    private final GenericModelMapper genericModelMapper;
    private final UniversityRepository universityRepository;

    @RabbitHandler
    public void consume(String trackingId) {
        log.info("Consuming phantom agent task queue {}", trackingId);

        Optional<PhantomAgentTaskEntity> optionalTask =
                phantomAgentTaskRepository.findById(UUID.fromString(trackingId));

        if (optionalTask.isEmpty()) {
            log.error("Task not found for trackingId: {}", trackingId);
            return;
        }

        PhantomAgentTaskEntity phantomAgentTaskEntity = optionalTask.get();
        try {
            LinkedInProfileScrapResponse response =
                    phantomBusterClient.getContainerOutput(phantomAgentTaskEntity.getContainerId());

            processResponse(phantomAgentTaskEntity, response);

        } catch (Exception e) {
            log.error("Error processing phantom agent task {}", trackingId, e);
            phantomAgentTaskEntity.setPhantomAgentTaskStatus(PhantomAgentTaskStatus.TASK_FAILED);
        }

        phantomAgentTaskRepository.save(phantomAgentTaskEntity);
    }

    public void processResponse(PhantomAgentTaskEntity phantomAgentTaskEntity, LinkedInProfileScrapResponse response) {
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
