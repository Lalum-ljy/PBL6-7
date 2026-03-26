package com.ljy.pbl6.service;

import com.ljy.pbl6.entity.Activity;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityService {
    Activity findById(Long id);
    List<Activity> findAll();
    List<Activity> findAll(int page, int size);
    Activity create(Activity activity);
    Activity update(Activity activity);
    void delete(Long id);
    List<Activity> findByStatus(Integer status);
    List<Activity> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    List<Activity> findByKeyword(String keyword);
    List<Activity> findByActivityDesc(String activityDesc);
    List<Activity> findByCreator(String username);
    List<Activity> findByHotStatus(Integer hotStatus);
}