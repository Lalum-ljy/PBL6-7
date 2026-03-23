package com.ljy.pbl6.mq;

import com.ljy.pbl6.config.RabbitConfig;
import com.ljy.pbl6.dto.ActivityStatusMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ActivityStatusProducer {

    private final RabbitTemplate rabbitTemplate;

    public ActivityStatusProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendStatusUpdateMessage(Long activityId, Integer newStatus, LocalDateTime triggerTime) {
        ActivityStatusMessage message = new ActivityStatusMessage(activityId, newStatus, triggerTime);
        rabbitTemplate.convertAndSend(RabbitConfig.ACTIVITY_STATUS_EXCHANGE, 
                RabbitConfig.ACTIVITY_STATUS_ROUTING_KEY, message);
    }
}