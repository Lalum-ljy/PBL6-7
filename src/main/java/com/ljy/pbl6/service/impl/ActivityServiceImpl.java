package com.ljy.pbl6.service.impl;

import com.ljy.pbl6.entity.Activity;
import com.ljy.pbl6.mapper.ActivityMapper;
import com.ljy.pbl6.service.ActivityService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityMapper activityMapper;

    public ActivityServiceImpl(ActivityMapper activityMapper) {
        this.activityMapper = activityMapper;
    }

    @Override
    public Activity findById(Long id) {
        return activityMapper.findById(id);
    }

    @Override
    public List<Activity> findAll() {
        return activityMapper.findAll();
    }

    @Override
    public Activity create(Activity activity) {
        activity.setCreateTime(LocalDateTime.now());
        activity.setUpdateTime(LocalDateTime.now());
        activityMapper.insert(activity);
        return activity;
    }

    @Override
    public Activity update(Activity activity) {
        activity.setUpdateTime(LocalDateTime.now());
        activityMapper.update(activity);
        return activity;
    }

    @Override
    public void delete(Long id) {
        activityMapper.delete(id);
    }

    @Override
    public List<Activity> findByStatus(Integer status) {
        return activityMapper.findByStatus(status);
    }

    @Override
    public List<Activity> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return activityMapper.findByTimeRange(startTime, endTime);
    }

    @Override
    public List<Activity> findByActivityName(String activityName) {
        return activityMapper.findByActivityName(activityName);
    }

    @Override
    public List<Activity> findByCreator(String username) {
        return activityMapper.findByCreator(username);
    }
}