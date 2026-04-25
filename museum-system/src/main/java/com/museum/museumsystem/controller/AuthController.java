package com.museum.museumsystem.controller;

import com.museum.museumsystem.dto.request.LoginRequest;
import com.museum.museumsystem.dto.request.ResetPasswordRequest;
import com.museum.museumsystem.dto.request.UserRegisterRequest;
import com.museum.museumsystem.dto.response.LoginResponse;
import com.museum.museumsystem.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("注册成功");
    }

    @PostMapping("/send-reset-code")
    public ResponseEntity<?> sendResetCode(@RequestBody Map<String, String> payload) {
        authService.sendResetCode(payload.get("username"), payload.get("email"));
        return ResponseEntity.ok("验证码已发送");
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<?> verifyResetCode(@RequestBody Map<String, String> payload) {
        authService.verifyResetCode(payload.get("username"), payload.get("code"));
        return ResponseEntity.ok("验证成功");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("密码重置成功");
    }
}
