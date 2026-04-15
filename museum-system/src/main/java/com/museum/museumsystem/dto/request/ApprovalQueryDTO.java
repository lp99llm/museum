package com.museum.museumsystem.dto.request;

import lombok.Data;

@Data
public class ApprovalQueryDTO {
    private String bizType;
    private Long bizId;
    private String approvalStatus; // PENDING, APPROVED, REJECTED
    private Long applicant;
    private Long current = 1L;
    private Long size = 10L;
}