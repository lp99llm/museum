package com.museum.museumsystem.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("disposal")
public class Disposal {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long artifactId;

    private String artifactCode;

    private String artifactName;

    private String status;

    private String currentStage;

    private LocalDate applyDate;

    private String applicant;

    private String applyReason;

    private String disposalType;

    private String evaluationReport;

    private String expertOpinion;

    private LocalDate publicStartTime;

    private LocalDate publicEndTime;

    private String publicResult;

    private String approvalOpinion;

    private String approver;

    private LocalDate approvalDate;

    private String recordStatus;

    private LocalDate recordDate;

    private String recordOrganization;

    private LocalDate disposalDate;

    private String operator;

    private String executionResult;

    private String archiveNo;

    private LocalDate archiveDate;

    private String remarks;

    @TableField(fill = FieldFill.INSERT)
    private Long createdBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
