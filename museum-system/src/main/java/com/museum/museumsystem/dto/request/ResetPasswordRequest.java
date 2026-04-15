package com.museum.museumsystem.dto.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class ResetPasswordRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String email;
    private String code;      // 验证码（可选）
    @NotBlank
    private String newPassword;
}