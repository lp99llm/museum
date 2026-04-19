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
@TableName("exhibition_evaluation")
public class ExhibitionEvaluation {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long exhibitionId;

    private Integer visitorCount;

    private BigDecimal feedbackScore;

    private Integer safetyIncidents;

    private Integer artifactDamageCount;

    private BigDecimal budgetActual;

    private BigDecimal revenue;

    private String mediaCoverage;

    private Integer socialMediaMentions;

    private String majorHighlights;

    private String problems;

    private String improvementSuggestions;

    private String evaluator;

    private LocalDate evaluationDate;

    private Long createdBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
