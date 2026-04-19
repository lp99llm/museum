package com.museum.museumsystem.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExhibitionEvaluationDTO {
    private Long id;
    private Long exhibitionId;
    private String exhibitionName;
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
}
