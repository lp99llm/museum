package com.museum.museumsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("approval")
public class Approval {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String bizType;
    private Long bizId;
    private String approvalStatus; // PENDING, APPROVED, REJECTED
    private Long applicant;
    private LocalDateTime applyTime;
    private Long approver;
    private LocalDateTime approveTime;
    private String approveOpinion;
    private String currentNode;
}