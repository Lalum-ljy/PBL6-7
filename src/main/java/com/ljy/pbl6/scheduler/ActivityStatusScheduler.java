package com.ljy.pbl6.scheduler;

import com.ljy.pbl6.entity.Activity;
import com.ljy.pbl6.mq.ActivityStatusProducer;
import com.ljy.pbl6.service.ActivityService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ActivityStatusScheduler {

    private final ActivityService activityService;
    private final ActivityStatusProducer activityStatusProducer;

    public ActivityStatusScheduler(ActivityService activityService, ActivityStatusProducer activityStatusProducer) {
        this.activityService = activityService;
        this.activityStatusProducer = activityStatusProducer;
    }

    // 每分钟检查一次活动状态
    @Scheduled(fixedRate = 60000)
    public void checkActivityStatus() {
        LocalDateTime now = LocalDateTime.now();
        
        // 获取所有未开始的活动
        List<Activity> notStartedActivities = activityService.findByStatus(0);
        for (Activity activity : notStartedActivities) {
            // 检查开始时间是否为null
            if (activity.getStartTime() != null) {
                // 如果到达开始时间，发送消息更新为进行中
                if (now.isAfter(activity.getStartTime()) || now.isEqual(activity.getStartTime())) {
                    activityStatusProducer.sendStatusUpdateMessage(activity.getId(), 1, activity.getStartTime());
                }
            }
        }
        
        // 获取所有进行中的活动
        List<Activity> ongoingActivities = activityService.findByStatus(1);
        for (Activity activity : ongoingActivities) {
            // 检查结束时间是否为null
            if (activity.getEndTime() != null) {
                // 如果到达结束时间，发送消息更新为已结束
                if (now.isAfter(activity.getEndTime()) || now.isEqual(activity.getEndTime())) {
                    activityStatusProducer.sendStatusUpdateMessage(activity.getId(), 2, activity.getEndTime());
                }
            }
        }
    }
}