package com.museum.museumsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("appraisal")
public class Appraisal {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long artifactId;
    private LocalDate appraisalDate;
    private String expertName;
    private String expertOpinion;
    private String appraisalLevel;
    private String appraisalResult;
    private LocalDateTime createdTime;
}