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
@TableName("storage_check")
public class StorageCheck {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long artifactId;

    private String artifactCode;

    private String artifactName;

    private LocalDate checkDate;

    private String checkType;

    private String checkResult;

    private String temperature;

    private String humidity;

    private String corrosionLevel;

    private String mildewLevel;

    private String brittlenessLevel;

    private String lightDamageLevel;

    private String pestDamageLevel;

    private String conditionDesc;

    private String handlingSuggestion;

    private LocalDate nextCheckDate;

    private Long checker;

    private String checkerName;

    private String remarks;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
