package com.ljy.pbl6.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ActivityStatusMessage implements Serializable {
    private Long activityId;
    private Integer newStatus;
    private LocalDateTime triggerTime;

    public ActivityStatusMessage() {
    }

    public ActivityStatusMessage(Long activityId, Integer newStatus, LocalDateTime triggerTime) {
        this.activityId = activityId;
        this.newStatus = newStatus;
        this.triggerTime = triggerTime;
    }
}