package com.museum.museumsystem.dto.request;

import lombok.Data;

@Data
public class RestorationQueryDTO {
    private Long artifactId;
    private String restorer;
    private Long current = 1L;
    private Long size = 10L;
}