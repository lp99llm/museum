package com.museum.museumsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("restoration")
public class Restoration {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long artifactId;
    private LocalDate restorationDate;
    private String restorer;
    private String restorationContent;
    private String beforeCondition;
    private String afterCondition;
    private BigDecimal cost;
    private LocalDateTime createdTime;
}