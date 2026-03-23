package com.ljy.pbl6.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private Integer userType;
    private String username;
    private String password;
    private String realName;
    private Integer gender;
    private String phone;
    private String email;
    private Long schoolId;
    private String college;
    private String major;
    private String grade;
    private String className;
}