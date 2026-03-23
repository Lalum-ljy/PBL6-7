package com.ljy.pbl6.mapper;

import com.ljy.pbl6.entity.SysUser;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysUserMapper {
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    SysUser findByUsername(String username);

    @Insert("INSERT INTO sys_user (user_type, username, password, real_name, gender, phone, email, school_id, college, major, grade, class_name, avatar, status, is_deleted, create_time, update_time) VALUES (#{userType}, #{username}, #{password}, #{realName}, #{gender}, #{phone}, #{email}, #{schoolId}, #{college}, #{major}, #{grade}, #{className}, #{avatar}, #{status}, #{isDeleted}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysUser user);

    @Update("UPDATE sys_user SET last_login_time = #{lastLoginTime}, last_login_ip = #{lastLoginIp} WHERE id = #{id}")
    int updateLastLoginInfo(SysUser user);
}