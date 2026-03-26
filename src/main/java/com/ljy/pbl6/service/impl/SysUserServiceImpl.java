package com.ljy.pbl6.service.impl;

import com.ljy.pbl6.dto.LoginDto;
import com.ljy.pbl6.dto.LoginResponseDto;
import com.ljy.pbl6.dto.RegisterDto;
import com.ljy.pbl6.entity.SysUser;
import com.ljy.pbl6.mapper.SysUserMapper;
import com.ljy.pbl6.service.SysUserService;
import com.ljy.pbl6.util.JwtUtil;
import com.ljy.pbl6.util.RedisUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private RedisUtil redisUtil;

    public SysUserServiceImpl(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    public void setRedisUtil(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Override
    public SysUser register(RegisterDto registerDto) {
        // 检查用户名是否已存在
        SysUser existingUser = sysUserMapper.findByUsername(registerDto.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建新用户
        SysUser user = new SysUser();
        user.setUserType(0); // 默认用户类型
        user.setUsername(registerDto.getUsername());
        // 密码哈希加密
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRealName("" + registerDto.getUsername()); // 使用用户名作为默认真实姓名
        user.setGender(0); // 默认性别
        user.setPhone(""); // 默认空
        user.setEmail("" + registerDto.getUsername() + "@example.com"); // 默认邮箱
        user.setSchoolId(0L); // 默认学校ID
        user.setCollege(""); // 默认空
        user.setMajor(""); // 默认空
        user.setGrade(""); // 默认空
        user.setClassName(""); // 默认空
        user.setStatus(1); // 默认为正常状态
        user.setIsDeleted(0); // 默认为未删除
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 保存用户
        sysUserMapper.insert(user);
        return user;
    }

    @Override
    public LoginResponseDto login(LoginDto loginDto, String ip) {
        // 根据用户名查询用户
        SysUser user = sysUserMapper.findByUsername(loginDto.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }

        // 更新最后登录时间和IP地址
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(ip);
        sysUserMapper.updateLastLoginInfo(user);

        // 生成token
        String token = JwtUtil.generateToken(user.getUsername());

        // 将token存储到Redis，有效期24小时
        if (redisUtil != null) {
            try {
                redisUtil.set("token:" + user.getUsername(), token, 24, TimeUnit.HOURS);
            } catch (Exception e) {
                // Redis操作失败，跳过Redis存储
                System.err.println("Redis存储token失败: " + e.getMessage());
            }
        }

        // 构建响应
        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setUser(user);

        return response;
    }
}