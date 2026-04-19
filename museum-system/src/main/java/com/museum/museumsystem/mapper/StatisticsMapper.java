package com.museum.museumsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.museum.museumsystem.entity.Artifact;
import com.museum.museumsystem.entity.Exhibition;
import com.museum.museumsystem.entity.VisitorAppointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface StatisticsMapper extends BaseMapper<Artifact> {

    @Select("SELECT COUNT(*) FROM artifact")
    Long getTotalArtifacts();

    @Select("SELECT COUNT(*) FROM exhibition")
    Long getTotalExhibitions();

    @Select("SELECT COUNT(*) FROM visitor_appointment WHERE status = 'APPROVED'")
    Long getTotalVisitors();

    @Select("SELECT COUNT(*) FROM visitor_appointment WHERE status = 'PENDING'")
    Long getPendingAppointments();

    @Select("SELECT COALESCE(SUM(budget_actual), 0) FROM exhibition_evaluation")
    Double getTotalBudget();

    @Select("SELECT COALESCE(SUM(revenue), 0) FROM exhibition_evaluation")
    Double getTotalRevenue();

    @Select("SELECT COALESCE(AVG(feedback_score), 0) FROM exhibition_evaluation")
    Double getAverageFeedbackScore();

    @Select("SELECT COALESCE(SUM(safety_incidents), 0) FROM exhibition_evaluation")
    Integer getTotalSafetyIncidents();

    @Select("SELECT category, COUNT(*) as count FROM artifact GROUP BY category ORDER BY count DESC")
    List<Map<String, Object>> getArtifactCategoryDistribution();

    @Select("SELECT current_state as status, COUNT(*) as count FROM artifact GROUP BY current_state ORDER BY count DESC")
    List<Map<String, Object>> getArtifactStatusDistribution();

    @Select("SELECT DATE_FORMAT(appointment_date, '%Y-%m') as month, SUM(visitor_count) as value " +
            "FROM visitor_appointment WHERE status = 'APPROVED' GROUP BY month ORDER BY month DESC LIMIT 12")
    List<Map<String, Object>> getVisitorTrend();

    @Select("SELECT e.id as exhibitionId, e.name as exhibitionName, " +
            "COALESCE(ve.visitor_count, 0) as visitorCount, " +
            "COALESCE(ve.feedback_score, 0) as feedbackScore " +
            "FROM exhibition e LEFT JOIN exhibition_evaluation ve ON e.id = ve.exhibition_id " +
            "WHERE e.status = 'FINISHED' ORDER BY ve.visitor_count DESC LIMIT 10")
    List<Map<String, Object>> getExhibitionEffects();

    @Select("<script>" +
            "SELECT * FROM artifact WHERE 1=1 " +
            "<if test='category != null and category != \"\"'> AND category = #{category} </if>" +
            "<if test='status != null and status != \"\"'> AND current_state = #{status} </if>" +
            "<if test='startDate != null'> AND created_time >= #{startDate} </if>" +
            "<if test='endDate != null'> AND created_time &lt;= #{endDate} </if>" +
            "</script>")
    List<Artifact> drillDownArtifacts(@Param("category") String category,
                                       @Param("status") String status,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);
}
