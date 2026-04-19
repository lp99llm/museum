package com.museum.museumsystem.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("appraisal")
public class Appraisal {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long artifactId;

    private String artifactCode;

    private String artifactName;

    private LocalDate appraisalDate;

    private String expertName;

    private String expertTitle;

    private String expertOpinion;

    private String appraisalLevel;

    private String appraisalResult;

    private BigDecimal appraisalFee;

    private BigDecimal estimatedValue;

    private String attachment;

    private String remarks;

    private Long createdBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
