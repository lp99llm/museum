package com.museum.museumsystem.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class StorageCheckQueryDTO {
    private Long artifactId;

    private String artifactCode;

    private String artifactName;

    private String checkType;

    private String checkResult;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long checker;

    private Long current = 1L;

    private Long size = 10L;
}
