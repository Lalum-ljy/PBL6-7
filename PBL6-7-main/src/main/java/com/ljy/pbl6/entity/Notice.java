package com.ljy.pbl6.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Notice {
    private Long id;
    private String title;
    private String content;
    private Integer readerstatus;
    private Integer status;
    private LocalDateTime createTime;
}
