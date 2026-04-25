package com.museum.museumsystem.service;

import com.museum.museumsystem.dto.request.StatisticsQueryDTO;
import com.museum.museumsystem.dto.response.AIAnalysisVO;
import com.museum.museumsystem.dto.response.DashboardVO;
import com.museum.museumsystem.dto.response.DrillDownVO;

import java.util.Map;

public interface StatisticsService {

    DashboardVO getDashboardData();

    Map<String, Object> getArtifactStats(StatisticsQueryDTO queryDTO);

    Map<String, Object> getExhibitionStats(StatisticsQueryDTO queryDTO);

    Map<String, Object> getVisitorStats(StatisticsQueryDTO queryDTO);

    Map<String, Object> getProcessStats(StatisticsQueryDTO queryDTO);

    Map<String, Object> getSpaceHeatmapStats();

    DrillDownVO drillDownArtifacts(StatisticsQueryDTO queryDTO);

    String generateAIAnalysis(String queryType);

    Map<String, Object> getExhibitionPerformance();

    Map<String, Object> getProcessEfficiencyDetail();

    AIAnalysisVO getAIAnalysis(String queryType);

    Map<String, Object> scheduleReport(String name, String cron, java.util.List<String> metrics, String receiver);
}
