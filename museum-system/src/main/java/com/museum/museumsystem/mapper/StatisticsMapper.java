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

    @Select("SELECT '出库' AS processName, ROUND(COALESCE(AVG(DATEDIFF(COALESCE(actual_return_date, CURDATE()), outbound_date)), 0), 1) AS avgDays FROM outbound WHERE outbound_date IS NOT NULL " +
            "UNION ALL " +
            "SELECT '修复' AS processName, ROUND(COALESCE(AVG(DATEDIFF(COALESCE(actual_end_date, CURDATE()), apply_date)), 0), 1) AS avgDays FROM restoration WHERE apply_date IS NOT NULL " +
            "UNION ALL " +
            "SELECT '外借' AS processName, ROUND(COALESCE(AVG(DATEDIFF(COALESCE(actual_return_date, CURDATE()), apply_date)), 0), 1) AS avgDays FROM loan WHERE apply_date IS NOT NULL " +
            "UNION ALL " +
            "SELECT '处置' AS processName, ROUND(COALESCE(AVG(DATEDIFF(COALESCE(archive_date, CURDATE()), apply_date)), 0), 1) AS avgDays FROM disposal WHERE apply_date IS NOT NULL " +
            "UNION ALL " +
            "SELECT '入库' AS processName, ROUND(COALESCE(AVG(DATEDIFF(storage_date, DATE(created_time))), 0), 1) AS avgDays FROM warehousing WHERE storage_date IS NOT NULL")
    List<Map<String, Object>> getProcessDurationStats();

    @Select("SELECT month, SUM(createdCount) AS createdCount, SUM(completedCount) AS completedCount FROM (" +
            "SELECT DATE_FORMAT(created_time, '%Y-%m') AS month, COUNT(*) AS createdCount, SUM(CASE WHEN status IN ('RETURNED','COMPLETED','APPROVED') THEN 1 ELSE 0 END) AS completedCount FROM outbound GROUP BY month " +
            "UNION ALL " +
            "SELECT DATE_FORMAT(created_time, '%Y-%m') AS month, COUNT(*) AS createdCount, SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS completedCount FROM restoration GROUP BY month " +
            "UNION ALL " +
            "SELECT DATE_FORMAT(created_time, '%Y-%m') AS month, COUNT(*) AS createdCount, SUM(CASE WHEN status = 'RETURNED' THEN 1 ELSE 0 END) AS completedCount FROM loan GROUP BY month " +
            "UNION ALL " +
            "SELECT DATE_FORMAT(created_time, '%Y-%m') AS month, COUNT(*) AS createdCount, SUM(CASE WHEN status = 'APPROVED' THEN 1 ELSE 0 END) AS completedCount FROM disposal GROUP BY month " +
            ") t GROUP BY month ORDER BY month DESC LIMIT 6")
    List<Map<String, Object>> getProcessMonthlyTrend();

    @Select("SELECT '出库' AS processName, COUNT(*) AS riskValue FROM outbound WHERE expected_return_date IS NOT NULL AND actual_return_date IS NULL AND expected_return_date < CURDATE() " +
            "UNION ALL " +
            "SELECT '修复' AS processName, COUNT(*) AS riskValue FROM restoration WHERE estimated_end_date IS NOT NULL AND actual_end_date IS NULL AND estimated_end_date < CURDATE() " +
            "UNION ALL " +
            "SELECT '外借' AS processName, COUNT(*) AS riskValue FROM loan WHERE expected_return_date IS NOT NULL AND actual_return_date IS NULL AND expected_return_date < CURDATE() " +
            "UNION ALL " +
            "SELECT '处置' AS processName, COUNT(*) AS riskValue FROM disposal WHERE status = 'REJECTED'")
    List<Map<String, Object>> getProcessRiskStats();

    @Select("SELECT COALESCE(location, '未设置') AS location, COALESCE(current_state, '未设置') AS state, COUNT(*) AS count " +
            "FROM artifact GROUP BY location, current_state ORDER BY location, current_state")
    List<Map<String, Object>> getSpaceHeatmapStats();

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
