package com.museum.museumsystem.dto.request;

import lombok.Data;

@Data
public class DisposalQueryDTO {
    private Long artifactId;
    private String disposalType;
    private Long current = 1L;
    private Long size = 10L;
}