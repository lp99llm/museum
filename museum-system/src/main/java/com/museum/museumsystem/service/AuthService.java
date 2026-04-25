package com.museum.museumsystem.service;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.museum.museumsystem.common.BusinessException;
import com.museum.museumsystem.dto.request.LoginRequest;
import com.museum.museumsystem.dto.request.ResetPasswordRequest;
import com.museum.museumsystem.dto.request.UserRegisterRequest;
import com.museum.museumsystem.dto.response.LoginResponse;
import com.museum.museumsystem.entity.Role;
import com.museum.museumsystem.entity.User;
import com.museum.museumsystem.mapper.RoleMapper;
import com.museum.museumsystem.mapper.UserMapper;
import com.museum.museumsystem.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class AuthService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserService userService;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        List<Long> roleIds = userRoleService.getRoleIdsByUserId(user.getId());
        String roles = "";
        if (!roleIds.isEmpty()) {
            roles = roleMapper.selectBatchIds(roleIds).stream()
                    .map(Role::getRoleCode)
                    .collect(Collectors.joining(","));
        }

        userService.updateLastLoginInfo(user.getId(), resolveRequestIp(), LocalDateTime.now());
        return new LoginResponse(token, user.getUsername(), roles);
    }

    @Transactional
    public void register(UserRegisterRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "用户名已存在");
        }

        wrapper.clear();
        wrapper.eq(User::getEmail, request.getEmail());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "邮箱已被注册");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        userMapper.insert(user);
    }

    public void sendResetCode(String username, String email) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (!email.equals(user.getEmail())) {
            throw new BusinessException(400, "用户名与邮箱不匹配");
        }

        String code = RandomUtil.randomNumbers(6);
        String redisKey = "reset:code:" + username;
        redisTemplate.opsForValue().set(redisKey, code, 5, TimeUnit.MINUTES);
        emailService.sendSimpleEmail(
                email,
                "密码重置验证码",
                "您的验证码是：" + code + "，5 分钟内有效。"
        );
    }

    public void verifyResetCode(String username, String code) {
        String redisKey = "reset:code:" + username;
        String savedCode = redisTemplate.opsForValue().get(redisKey);
        if (savedCode == null) {
            throw new BusinessException(400, "验证码已过期，请重新获取");
        }
        if (!savedCode.equals(code)) {
            throw new BusinessException(400, "验证码错误");
        }
        redisTemplate.delete(redisKey);
        redisTemplate.opsForValue().set("reset:verified:" + username, "true", 5, TimeUnit.MINUTES);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String redisKey = "reset:code:" + request.getUsername();
        String verifiedKey = "reset:verified:" + request.getUsername();
        String savedCode = redisTemplate.opsForValue().get(redisKey);
        String verified = redisTemplate.opsForValue().get(verifiedKey);
        boolean codeVerified = "true".equals(verified);

        if (!codeVerified) {
            if (savedCode == null) {
                throw new BusinessException(400, "验证码已过期，请重新获取");
            }
            if (!savedCode.equals(request.getCode())) {
                throw new BusinessException(400, "验证码错误");
            }
        }

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (!request.getEmail().equals(user.getEmail())) {
            throw new BusinessException(400, "邮箱不匹配");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userMapper.updateById(user);
        redisTemplate.delete(redisKey);
        redisTemplate.delete(verifiedKey);
    }

    private String resolveRequestIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null || attributes.getRequest() == null) {
            return "unknown";
        }
        String forwarded = attributes.getRequest().getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.trim().isEmpty()) {
            int commaIndex = forwarded.indexOf(',');
            return commaIndex > 0 ? forwarded.substring(0, commaIndex).trim() : forwarded.trim();
        }
        return attributes.getRequest().getRemoteAddr();
    }
}
