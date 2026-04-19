package com.museum.museumsystem.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class WarehousingQueryDTO {
    private Long artifactId;

    private String artifactCode;

    private String artifactName;

    private String storageArea;

    private String storageStatus;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long current = 1L;

    private Long size = 10L;
}
