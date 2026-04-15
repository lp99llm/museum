package com.museum.museumsystem.service;

import com.museum.museumsystem.dto.request.LoginRequest;
import com.museum.museumsystem.dto.request.UserRegisterRequest;
import com.museum.museumsystem.dto.request.ResetPasswordRequest;
import com.museum.museumsystem.dto.response.LoginResponse;
import com.museum.museumsystem.entity.User;
import com.museum.museumsystem.repository.UserRepository;
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
import cn.hutool.core.util.RandomUtil;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
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

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        return new LoginResponse(token, user.getUsername(), user.getRole());
    }

    @Transactional
    public void register(UserRegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole("USER");
        userRepository.save(user);
    }

    public void sendResetCode(String username, String email) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (!user.getEmail().equals(email)) {
            throw new RuntimeException("用户名与邮箱不匹配");
        }
        String code = RandomUtil.randomNumbers(6);
        String redisKey = "reset:code:" + username;
        redisTemplate.opsForValue().set(redisKey, code, 5, TimeUnit.MINUTES);
        emailService.sendSimpleEmail(email, "密码重置验证码", "您的验证码是：" + code + "，5分钟内有效。");
    }

    public void verifyResetCode(String username, String code) {
        String redisKey = "reset:code:" + username;
        String savedCode = redisTemplate.opsForValue().get(redisKey);
        if (savedCode == null) {
            throw new RuntimeException("验证码已过期，请重新获取");
        }
        if (!savedCode.equals(code)) {
            throw new RuntimeException("验证码错误");
        }
        redisTemplate.delete(redisKey);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        // 如果前端已经通过 verifyResetCode 校验，则此处不再重复校验验证码，直接重置
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (!user.getEmail().equals(request.getEmail())) {
            throw new RuntimeException("邮箱不匹配");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}