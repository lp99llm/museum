package com.museum.museumsystem.dto.request;

import lombok.Data;

@Data
public class AppraisalQueryDTO {
    private Long artifactId;
    private String appraisalLevel;
    private Long current = 1L;
    private Long size = 10L;
}