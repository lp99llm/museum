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
@TableName("loan")
public class Loan {
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

    private String borrowerInstitution;

    private String borrowerContact;

    private String borrowerPhone;

    private String borrowerAddress;

    private LocalDate loanDate;

    private LocalDate expectedReturnDate;

    private LocalDate actualReturnDate;

    private String loanAgreement;

    private String loanPurpose;

    private String transportMethod;

    private String securityMeasures;

    private String loanLocation;

    private String loanSupervisor;

    private String beforeCondition;

    private String afterCondition;

    private String returnImage;

    private String returnRemarks;

    private String approvalOpinion;

    private String handler;

    @TableField(fill = FieldFill.INSERT)
    private Long createdBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
