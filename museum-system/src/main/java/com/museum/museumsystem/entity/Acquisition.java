package com.museum.museumsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("acquisition")
public class Acquisition {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long artifactId;
    private LocalDate acquisitionDate;
    private String acquisitionType; // 捐赠/购买/移交
    private BigDecimal acquisitionCost;
    private String donorInfo;
    private String remarks;
    private Long createdBy;
    private LocalDateTime createdTime;
}