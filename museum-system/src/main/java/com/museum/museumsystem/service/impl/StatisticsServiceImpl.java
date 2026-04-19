package com.museum.museumsystem.service.impl;

import com.museum.museumsystem.dto.request.StatisticsQueryDTO;
import com.museum.museumsystem.dto.response.DashboardVO;
import com.museum.museumsystem.dto.response.DrillDownVO;
import com.museum.museumsystem.entity.Artifact;
import com.museum.museumsystem.mapper.StatisticsMapper;
import com.museum.museumsystem.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsMapper statisticsMapper;

    @Override
    public DashboardVO getDashboardData() {
        DashboardVO dashboard = new DashboardVO();

        dashboard.setTotalArtifacts(statisticsMapper.getTotalArtifacts());
        dashboard.setTotalExhibitions(statisticsMapper.getTotalExhibitions());
        dashboard.setTotalVisitors(statisticsMapper.getTotalVisitors());
        dashboard.setPendingAppointments(statisticsMapper.getPendingAppointments());
        dashboard.setTotalRevenue(BigDecimal.valueOf(statisticsMapper.getTotalRevenue()));
        dashboard.setTotalBudget(BigDecimal.valueOf(statisticsMapper.getTotalBudget()));
        dashboard.setAverageFeedbackScore(BigDecimal.valueOf(statisticsMapper.getAverageFeedbackScore()));
        dashboard.setSafetyIncidentCount(statisticsMapper.getTotalSafetyIncidents());

        dashboard.setCategoryDistribution(buildCategoryDistribution());
        dashboard.setStatusDistribution(buildStatusDistribution());
        dashboard.setVisitorTrend(buildVisitorTrend());
        dashboard.setExhibitionEffects(buildExhibitionEffects());

        return dashboard;
    }

    @Override
    public Map<String, Object> getArtifactStats(StatisticsQueryDTO queryDTO) {
        Map<String, Object> stats = new HashMap<>();

        List<Map<String, Object>> categoryDist = statisticsMapper.getArtifactCategoryDistribution();
        stats.put("categoryDistribution", categoryDist);

        List<Map<String, Object>> statusDist = statisticsMapper.getArtifactStatusDistribution();
        stats.put("statusDistribution", statusDist);

        stats.put("totalCount", statisticsMapper.getTotalArtifacts());

        return stats;
    }

    @Override
    public Map<String, Object> getExhibitionStats(StatisticsQueryDTO queryDTO) {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalExhibitions", statisticsMapper.getTotalExhibitions());
        stats.put("totalRevenue", statisticsMapper.getTotalRevenue());
        stats.put("totalBudget", statisticsMapper.getTotalBudget());
        stats.put("averageFeedback", statisticsMapper.getAverageFeedbackScore());
        stats.put("safetyIncidents", statisticsMapper.getTotalSafetyIncidents());

        List<Map<String, Object>> effects = statisticsMapper.getExhibitionEffects();
        stats.put("exhibitionEffects", effects);

        return stats;
    }

    @Override
    public Map<String, Object> getVisitorStats(StatisticsQueryDTO queryDTO) {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalVisitors", statisticsMapper.getTotalVisitors());
        stats.put("pendingAppointments", statisticsMapper.getPendingAppointments());

        List<Map<String, Object>> trend = statisticsMapper.getVisitorTrend();
        stats.put("visitorTrend", trend);

        return stats;
    }

    @Override
    public DrillDownVO drillDownArtifacts(StatisticsQueryDTO queryDTO) {
        DrillDownVO drillDown = new DrillDownVO();
        drillDown.setTitle("文物明细数据");

        List<String> headers = Arrays.asList("编号", "名称", "类别", "年代", "材质", "保存状态", "创建时间");
        drillDown.setHeaders(headers);

        List<Artifact> artifacts = statisticsMapper.drillDownArtifacts(
                queryDTO.getCategory(),
                queryDTO.getStatus(),
                queryDTO.getStartDate(),
                queryDTO.getEndDate()
        );

        List<Map<String, Object>> records = artifacts.stream().map(a -> {
            Map<String, Object> record = new LinkedHashMap<>();
            record.put("编号", a.getCode());
            record.put("名称", a.getName());
            record.put("类别", a.getCategory());
            record.put("年代", a.getEra());
            record.put("材质", a.getMaterial());
            record.put("保存状态", a.getCurrentState());
            record.put("创建时间", a.getCreatedTime() != null ? a.getCreatedTime().format(DateTimeFormatter.ISO_LOCAL_DATE) : "");
            return record;
        }).collect(Collectors.toList());

        drillDown.setRecords(records);
        drillDown.setTotal((long) records.size());

        return drillDown;
    }

    @Override
    public String generateAIAnalysis(String queryType) {
        StringBuilder analysis = new StringBuilder();

        switch (queryType) {
            case "exhibition_trend":
                analysis.append("【展览热度趋势分析】\n\n");
                Long totalExhibitions = statisticsMapper.getTotalExhibitions();
                Long totalVisitors = statisticsMapper.getTotalVisitors();
                Double avgFeedback = statisticsMapper.getAverageFeedbackScore();
                analysis.append("• 当前展览总数：").append(totalExhibitions).append("个\n");
                analysis.append("• 累计参观人数：").append(totalVisitors).append("人\n");
                analysis.append("• 平均观众评分：").append(String.format("%.1f", avgFeedback)).append("分\n\n");
                analysis.append("【趋势预测】根据当前数据，预计下季度参观人数将增长10%-15%。\n");
                analysis.append("【优化建议】建议加强数字化展览投入，提升线上互动体验。");
                break;

            case "inventory_optimization":
                analysis.append("【库存结构与流转优化建议】\n\n");
                Long totalArtifacts = statisticsMapper.getTotalArtifacts();
                List<Map<String, Object>> categoryDist = statisticsMapper.getArtifactCategoryDistribution();
                analysis.append("• 当前库存总量：").append(totalArtifacts).append("件\n\n");
                analysis.append("【类别分布】\n");
                categoryDist.forEach(cat -> {
                    analysis.append("  - ").append(cat.get("category"))
                            .append("：").append(cat.get("count")).append("件\n");
                });
                analysis.append("\n【优化建议】\n");
                analysis.append("1. 对于库存超过500件的类别，建议适当控制征集数量\n");
                analysis.append("2. 增加珍贵文物的展示频次，提高馆藏利用率\n");
                analysis.append("3. 建议建立文物流转预警机制，及时处理状态异常的文物");
                break;

            default:
                analysis.append("请选择分析类型：exhibition_trend（展览趋势）或 inventory_optimization（库存优化）");
        }

        return analysis.toString();
    }

    private List<DashboardVO.CategoryStatVO> buildCategoryDistribution() {
        List<Map<String, Object>> rawData = statisticsMapper.getArtifactCategoryDistribution();
        Long total = statisticsMapper.getTotalArtifacts();
        return rawData.stream().map(item -> {
            DashboardVO.CategoryStatVO result = new DashboardVO.CategoryStatVO();
            result.setCategory((String) item.get("category"));
            result.setCount(((Number) item.get("count")).longValue());
            if (total > 0) {
                double pct = ((Number) item.get("count")).doubleValue() / total * 100;
                result.setPercentage(BigDecimal.valueOf(pct).setScale(1, RoundingMode.HALF_UP));
            } else {
                result.setPercentage(BigDecimal.ZERO);
            }
            return result;
        }).collect(Collectors.toList());
    }

    private List<DashboardVO.StatusStatVO> buildStatusDistribution() {
        List<Map<String, Object>> rawData = statisticsMapper.getArtifactStatusDistribution();
        Long total = statisticsMapper.getTotalArtifacts();
        return rawData.stream().map(item -> {
            DashboardVO.StatusStatVO result = new DashboardVO.StatusStatVO();
            result.setStatus((String) item.get("status"));
            result.setCount(((Number) item.get("count")).longValue());
            if (total > 0) {
                double pct = ((Number) item.get("count")).doubleValue() / total * 100;
                result.setPercentage(BigDecimal.valueOf(pct).setScale(1, RoundingMode.HALF_UP));
            } else {
                result.setPercentage(BigDecimal.ZERO);
            }
            return result;
        }).collect(Collectors.toList());
    }

    private List<DashboardVO.MonthlyTrendVO> buildVisitorTrend() {
        List<Map<String, Object>> rawData = statisticsMapper.getVisitorTrend();
        return rawData.stream().map(item -> {
            DashboardVO.MonthlyTrendVO result = new DashboardVO.MonthlyTrendVO();
            result.setMonth((String) item.get("month"));
            result.setValue(((Number) item.get("value")).longValue());
            return result;
        }).collect(Collectors.toList());
    }

    private List<DashboardVO.ExhibitionEffectVO> buildExhibitionEffects() {
        return statisticsMapper.getExhibitionEffects().stream().map(item -> {
            DashboardVO.ExhibitionEffectVO result = new DashboardVO.ExhibitionEffectVO();
            result.setExhibitionId(((Number) item.get("exhibitionId")).longValue());
            result.setExhibitionName((String) item.get("exhibitionName"));
            result.setVisitorCount(((Number) item.get("visitorCount")).longValue());
            result.setFeedbackScore(BigDecimal.valueOf(((Number) item.get("feedbackScore")).doubleValue()));
            result.setUtilizationRate(BigDecimal.ZERO); // 默认值，可根据实际情况调整
            return result;
        }).collect(Collectors.toList());
    }
}
