package com.museum.museumsystem.dto.request;

import lombok.Data;

@Data
public class LoanQueryDTO {
    private Long artifactId;
    private String status; // LOANED, RETURNED
    private Long current = 1L;
    private Long size = 10L;
}