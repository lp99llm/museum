package com.museum.museumsystem.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardVO {
    private Long totalArtifacts;
    private Long totalExhibitions;
    private Long totalVisitors;
    private Long pendingAppointments;

    private List<CategoryStatVO> categoryDistribution;
    private List<StatusStatVO> statusDistribution;
    private List<MonthlyTrendVO> visitorTrend;
    private List<ExhibitionEffectVO> exhibitionEffects;

    private BigDecimal totalRevenue;
    private BigDecimal totalBudget;
    private Integer safetyIncidentCount;
    private BigDecimal averageFeedbackScore;

    @Data
    public static class CategoryStatVO {
        private String category;
        private Long count;
        private BigDecimal percentage;
    }

    @Data
    public static class StatusStatVO {
        private String status;
        private Long count;
        private BigDecimal percentage;
    }

    @Data
    public static class MonthlyTrendVO {
        private String month;
        private Long value;
    }

    @Data
    public static class ExhibitionEffectVO {
        private Long exhibitionId;
        private String exhibitionName;
        private Long visitorCount;
        private BigDecimal feedbackScore;
        private BigDecimal utilizationRate;
    }
}
