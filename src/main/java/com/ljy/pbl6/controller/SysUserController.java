package com.ljy.pbl6.controller;

import com.ljy.pbl6.common.Response;
import com.ljy.pbl6.dto.LoginDto;
import com.ljy.pbl6.dto.LoginResponseDto;
import com.ljy.pbl6.dto.RegisterDto;
import com.ljy.pbl6.entity.SysUser;
import com.ljy.pbl6.service.SysUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class SysUserController {

    private final SysUserService sysUserService;

    public SysUserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @PostMapping("/register")
    public Response<SysUser> register(@RequestBody RegisterDto registerDto) {
        try {
            SysUser user = sysUserService.register(registerDto);
            return Response.success(user);
        } catch (RuntimeException e) {
            return Response.error(400, e.getMessage());
        }
    }

    @PostMapping("/login")
    public Response<LoginResponseDto> login(@RequestBody LoginDto loginDto, HttpServletRequest request) {
        try {
            String ip = getClientIp(request);
            LoginResponseDto response = sysUserService.login(loginDto, ip);
            return Response.success(response);
        } catch (RuntimeException e) {
            return Response.error(400, e.getMessage());
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}