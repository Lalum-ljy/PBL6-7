package com.ljy.pbl6.mapper;

import com.ljy.pbl6.entity.Activity;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ActivityMapper {
    @Select("SELECT * FROM sys_activity WHERE id = #{id}")
    Activity findById(Long id);

    @Select("SELECT * FROM sys_activity")
    List<Activity> findAll();

    @Select("SELECT * FROM sys_activity LIMIT #{offset}, #{limit}")
    List<Activity> findAllByPage(int offset, int limit);

    @Insert("INSERT INTO sys_activity (activity_name, activity_desc, cover_url, start_time, end_time, status, hot_status, creator, create_time, update_time) VALUES (#{activityName}, #{activityDesc}, #{coverUrl}, #{startTime}, #{endTime}, #{status}, #{hotStatus}, #{creator}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Activity activity);

    @Update("UPDATE sys_activity SET activity_name = #{activityName}, activity_desc = #{activityDesc}, cover_url = #{coverUrl}, start_time = #{startTime}, end_time = #{endTime}, status = #{status}, hot_status = #{hotStatus}, creator = #{creator}, update_time = #{updateTime} WHERE id = #{id}")
    int update(Activity activity);

    @Delete("DELETE FROM sys_activity WHERE id = #{id}")
    int delete(Long id);

    @Select("SELECT * FROM sys_activity WHERE status = #{status}")
    List<Activity> findByStatus(Integer status);

    @Select("SELECT * FROM sys_activity WHERE start_time >= #{startTime} AND end_time <= #{endTime}")
    List<Activity> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    @Select("SELECT * FROM sys_activity WHERE activity_name LIKE CONCAT('%', #{keyword}, '%') OR activity_desc LIKE CONCAT('%', #{keyword}, '%')")
    List<Activity> findByKeyword(String keyword);

    @Select("SELECT * FROM sys_activity WHERE activity_desc LIKE CONCAT('%', #{activityDesc}, '%')")
    List<Activity> findByActivityDesc(String activityDesc);

    @Select("SELECT a.* FROM sys_activity a JOIN sys_user u ON a.creator = u.username WHERE u.username LIKE CONCAT('%', #{username}, '%')")
    List<Activity> findByCreator(String username);

    @Select("SELECT * FROM sys_activity WHERE hot_status = #{hotStatus} AND status IN (0, 1)")
    List<Activity> findByHotStatus(Integer hotStatus);
}