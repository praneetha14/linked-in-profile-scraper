package com.profile.searcher.amqp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class PhantomAgentTaskPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(UUID trackingId) {
        rabbitTemplate.convertAndSend(
                "phantom-agent-delay-exchange",
                "phantom-agent-delay-routing-key",
                trackingId.toString(),
                message -> {
                    message.getMessageProperties().setHeader("x-delay", 300000);
                    return message;
                }
        );
        log.info("Publishing phantom-agent-task: {}", trackingId);
    }
}
