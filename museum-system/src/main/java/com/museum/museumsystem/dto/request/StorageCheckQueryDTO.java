package com.museum.museumsystem.dto.request;

import lombok.Data;

@Data
public class StorageCheckQueryDTO {
    private Long artifactId;
    private String checkResult; // 正常/异常/需修复
    private Long checker;
    private Long current = 1L;
    private Long size = 10L;
}