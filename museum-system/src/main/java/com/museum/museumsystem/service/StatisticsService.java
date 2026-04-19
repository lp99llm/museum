package com.museum.museumsystem.service;

import com.museum.museumsystem.dto.request.StatisticsQueryDTO;
import com.museum.museumsystem.dto.response.DashboardVO;
import com.museum.museumsystem.dto.response.DrillDownVO;

import java.util.Map;

public interface StatisticsService {

    DashboardVO getDashboardData();

    Map<String, Object> getArtifactStats(StatisticsQueryDTO queryDTO);

    Map<String, Object> getExhibitionStats(StatisticsQueryDTO queryDTO);

    Map<String, Object> getVisitorStats(StatisticsQueryDTO queryDTO);

    DrillDownVO drillDownArtifacts(StatisticsQueryDTO queryDTO);

    String generateAIAnalysis(String queryType);
}
