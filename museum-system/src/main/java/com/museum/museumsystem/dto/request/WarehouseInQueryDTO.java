package com.museum.museumsystem.dto.request;

import lombok.Data;

@Data
public class WarehouseInQueryDTO {
    private Long artifactId;
    private Long operator;
    private Long current = 1L;
    private Long size = 10L;
}