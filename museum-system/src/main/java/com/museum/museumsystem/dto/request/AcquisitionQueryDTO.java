package com.museum.museumsystem.dto.request;

import lombok.Data;

@Data
public class AcquisitionQueryDTO {
    private Long artifactId;
    private String acquisitionType;
    private Long current = 1L;
    private Long size = 10L;
}