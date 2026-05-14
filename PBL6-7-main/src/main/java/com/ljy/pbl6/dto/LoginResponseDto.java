package com.ljy.pbl6.dto;

import com.ljy.pbl6.entity.SysUser;
import lombok.Data;

@Data
public class LoginResponseDto {
    private String token;
    private SysUser user;
}