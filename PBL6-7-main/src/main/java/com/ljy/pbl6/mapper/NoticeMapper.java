package com.ljy.pbl6.mapper;

import com.ljy.pbl6.entity.Notice;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface NoticeMapper {
    @Select("SELECT * FROM sys_notice WHERE id = #{id}")
    Notice findById(Long id);

    @Select("SELECT * FROM sys_notice")
    List<Notice> findAll();

    @Insert("INSERT INTO sys_notice (title, content, readerstatus, status, create_time) VALUES (#{title}, #{content}, #{readerstatus}, #{status}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notice notice);

    @Update("UPDATE sys_notice SET title = #{title}, content = #{content}, readerstatus = #{readerstatus}, status = #{status} WHERE id = #{id}")
    int update(Notice notice);

    @Delete("DELETE FROM sys_notice WHERE id = #{id}")
    int delete(Long id);
}
