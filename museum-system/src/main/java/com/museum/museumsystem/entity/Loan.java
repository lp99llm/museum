package com.museum.museumsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
    private String borrowerInstitution;
    private String borrowerContact;
    private LocalDate loanDate;
    private LocalDate expectedReturnDate;
    private LocalDate actualReturnDate;
    private String loanAgreement;
    private String status; // LOANED, RETURNED
    private Long createdBy;
    private LocalDateTime createdTime;
}