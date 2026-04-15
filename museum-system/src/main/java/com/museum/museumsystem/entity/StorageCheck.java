package com.museum.museumsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("storage_check")
public class StorageCheck {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long artifactId;
    private LocalDate checkDate;
    private String checkResult; // 正常/异常/需修复
    private String conditionDesc;
    private Long checker;
    private LocalDateTime createdTime;
}