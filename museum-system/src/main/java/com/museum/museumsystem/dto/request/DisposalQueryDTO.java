package com.museum.museumsystem.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DisposalQueryDTO {
    private Long artifactId;

    private String artifactCode;

    private String artifactName;

    private String disposalType;

    private String status;

    private String currentStage;

    private String applicant;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long current = 1L;

    private Long size = 10L;
}
