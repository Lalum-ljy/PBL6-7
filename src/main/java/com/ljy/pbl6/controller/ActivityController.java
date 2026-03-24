package com.ljy.pbl6.controller;

import com.ljy.pbl6.common.Response;
import com.ljy.pbl6.entity.Activity;
import com.ljy.pbl6.service.ActivityService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/{id}")
    public Response<Activity> findById(@PathVariable Long id) {
        Activity activity = activityService.findById(id);
        return Response.success(activity);
    }

    @GetMapping
    public Response<List<Activity>> findAll() {
        List<Activity> activities = activityService.findAll();
        return Response.success(activities);
    }

    @PostMapping
    public Response<Activity> create(@RequestBody Activity activity) {
        Activity createdActivity = activityService.create(activity);
        return Response.success(createdActivity);
    }

    @PutMapping
    public Response<Activity> update(@RequestBody Activity activity) {
        Activity updatedActivity = activityService.update(activity);
        return Response.success(updatedActivity);
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        activityService.delete(id);
        return Response.success(null);
    }

    @GetMapping("/status/{status}")
    public Response<List<Activity>> findByStatus(@PathVariable Integer status) {
        List<Activity> activities = activityService.findByStatus(status);
        return Response.success(activities);
    }

    @GetMapping("/time-range")
    public Response<List<Activity>> findByTimeRange(@RequestParam LocalDateTime startTime, @RequestParam LocalDateTime endTime) {
        List<Activity> activities = activityService.findByTimeRange(startTime, endTime);
        return Response.success(activities);
    }

    @GetMapping("/search")
    public Response<List<Activity>> findByActivityName(@RequestParam String activityName) {
        List<Activity> activities = activityService.findByActivityName(activityName);
        return Response.success(activities);
    }

    @GetMapping("/creator")
    public Response<List<Activity>> findByCreator(@RequestParam String username) {
        List<Activity> activities = activityService.findByCreator(username);
        return Response.success(activities);
    }
}