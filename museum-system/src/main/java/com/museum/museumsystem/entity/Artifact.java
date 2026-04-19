package com.museum.museumsystem.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("artifact")
public class Artifact {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String name;
    private String category;
    private String era;
    private String material;
    private String size;
    private Double weight;
    private String source;
    private String preservationStatus;
    private String appraisalLevel;
    private String currentState;
    private String imageUrl;
    private String documents;
    private String environmentRequirements;
    private String insuranceInfo;
    private String location;
    private String currentLocation;
    private String description;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
