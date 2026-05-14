package com.ljy.pbl6.service;

import com.ljy.pbl6.dto.LoginDto;
import com.ljy.pbl6.dto.LoginResponseDto;
import com.ljy.pbl6.dto.RegisterDto;
import com.ljy.pbl6.entity.SysUser;

public interface SysUserService {
    SysUser register(RegisterDto registerDto);
    LoginResponseDto login(LoginDto loginDto, String ip);
}