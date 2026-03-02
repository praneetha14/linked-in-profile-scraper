package com.profile.searcher;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "phantom-agent-exchange";
    public static final String QUEUE_NAME = "phantom-agent-task-queue";
    public static final String ROUTING_KEY = "phantom-agent-task-routing-key";

    public static final String DELAY_EXCHANGE_NAME = "phantom-agent-delay-exchange";
    public static final String DELAY_QUEUE_NAME = "phantom-agent-delay-queue";
    public static final String DELAY_ROUTING_KEY = "phantom-agent-delay-routing-key";

    // Main Exchange (Destination)
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    // Main Queue (Consumer listens here)
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }

    // Binding Main Queue to Main Exchange
    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    // Delay Exchange (Producer sends here)
    @Bean
    public DirectExchange delayExchange() {
        return new DirectExchange(DELAY_EXCHANGE_NAME);
    }

    // Delay Queue (Holds messages for TTL)
    @Bean
    public Queue delayQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", EXCHANGE_NAME);
        args.put("x-dead-letter-routing-key", ROUTING_KEY);
        args.put("x-message-ttl", 300000); // 5 minutes delay
        return new Queue(DELAY_QUEUE_NAME, true, false, false, args);
    }

    // Binding Delay Queue to Delay Exchange
    @Bean
    public Binding delayBinding(Queue delayQueue, DirectExchange delayExchange) {
        return BindingBuilder.bind(delayQueue).to(delayExchange).with(DELAY_ROUTING_KEY);
    }
}
