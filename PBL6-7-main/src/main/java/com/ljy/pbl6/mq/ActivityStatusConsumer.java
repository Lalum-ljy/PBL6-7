package com.ljy.pbl6.mq;

import com.ljy.pbl6.dto.ActivityStatusMessage;
import com.ljy.pbl6.entity.Activity;
import com.ljy.pbl6.service.ActivityService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ActivityStatusConsumer {

    private final ActivityService activityService;

    public ActivityStatusConsumer(ActivityService activityService) {
        this.activityService = activityService;
    }

    @RabbitListener(queues = "activity.status.queue")
    public void handleStatusUpdate(ActivityStatusMessage message) {
        System.out.println("收到活动状态更新消息: " + message);
        
        // 检查是否到达触发时间
        if (LocalDateTime.now().isAfter(message.getTriggerTime()) || 
            LocalDateTime.now().isEqual(message.getTriggerTime())) {
            
            Activity activity = activityService.findById(message.getActivityId());
            if (activity != null) {
                // 只有未取消的活动才更新状态
                if (activity.getStatus() != 3) {
                    activity.setStatus(message.getNewStatus());
                    activityService.update(activity);
                    System.out.println("活动 " + message.getActivityId() + " 状态已更新为: " + message.getNewStatus());
                }
            }
        }
    }
}