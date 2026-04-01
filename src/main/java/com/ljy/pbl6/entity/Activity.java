package com.ljy.pbl6.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Activity {
    private Long id;
    private String activityName;
    private String activityDesc;
    private String coverUrl;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status = 0;
    private Integer hotStatus = 0;
    private String creator;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}