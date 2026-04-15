package com.museum.museumsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
    private LocalDate disposalDate;
    private String disposalType; // 移交/销毁/退藏
    private String reason;
    private String approvalDoc;
    private Long operator;
    private LocalDateTime createdTime;
}