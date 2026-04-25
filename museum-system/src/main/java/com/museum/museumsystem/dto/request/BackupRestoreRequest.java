package com.museum.museumsystem.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BackupRestoreRequest {
    @NotBlank(message = "备份文件名不能为空")
    private String fileName;
}
