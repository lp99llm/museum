package com.museum.museumsystem.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserResetPasswordDTO {
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
