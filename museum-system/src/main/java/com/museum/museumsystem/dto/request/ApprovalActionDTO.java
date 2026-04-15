package com.museum.museumsystem.dto.request;

import lombok.Data;

@Data
public class ApprovalActionDTO {
    private Long approvalId;
    private String approvalStatus; // APPROVED 或 REJECTED
    private String approveOpinion;
}