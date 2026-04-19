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
@TableName("restoration")
public class Restoration {
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

    private String damageCondition;

    private String restorationType;

    private String restorationPriority;

    private String planContent;

    private String planMaterials;

    private String planSteps;

    private LocalDate planDate;

    private String planApprover;

    private LocalDate planApproveDate;

    private LocalDate estimatedStartDate;

    private LocalDate estimatedEndDate;

    private LocalDate actualStartDate;

    private LocalDate actualEndDate;

    private String restorationContent;

    private String beforeCondition;

    private String afterCondition;

    private String restorationResult;

    private String beforeImages;

    private String afterImages;

    private String processVideo;

    private String restorer;

    private String restorerQualification;

    private String supervisor;

    private String acceptanceOpinion;

    private String acceptanceResult;

    private LocalDate acceptanceDate;

    private String remarks;

    private String handler;

    @TableField(fill = FieldFill.INSERT)
    private Long createdBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
