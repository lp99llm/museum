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
@TableName("warehousing")
public class Warehousing {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long artifactId;

    private String artifactCode;

    private String artifactName;

    private LocalDate storageDate;

    private String storageArea;

    private String storageShelf;

    private String storagePosition;

    private String storageStatus;

    private String temperature;

    private String humidity;

    private String handler;

    private String remarks;

    private Long createdBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
