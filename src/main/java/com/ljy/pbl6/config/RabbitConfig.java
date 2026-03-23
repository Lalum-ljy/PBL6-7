package com.ljy.pbl6.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String ACTIVITY_STATUS_QUEUE = "activity.status.queue";
    public static final String ACTIVITY_STATUS_EXCHANGE = "activity.status.exchange";
    public static final String ACTIVITY_STATUS_ROUTING_KEY = "activity.status.update";

    @Bean
    public Queue activityStatusQueue() {
        return new Queue(ACTIVITY_STATUS_QUEUE, true);
    }

    @Bean
    public DirectExchange activityStatusExchange() {
        return new DirectExchange(ACTIVITY_STATUS_EXCHANGE);
    }

    @Bean
    public Binding activityStatusBinding(Queue activityStatusQueue, DirectExchange activityStatusExchange) {
        return BindingBuilder.bind(activityStatusQueue)
                .to(activityStatusExchange)
                .with(ACTIVITY_STATUS_ROUTING_KEY);
    }
}