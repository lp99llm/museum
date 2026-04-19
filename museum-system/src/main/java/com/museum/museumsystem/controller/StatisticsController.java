package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.dto.request.StatisticsQueryDTO;
import com.museum.museumsystem.dto.response.DashboardVO;
import com.museum.museumsystem.dto.response.DrillDownVO;
import com.museum.museumsystem.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/dashboard")
    public Result<DashboardVO> getDashboard() {
        return Result.success(statisticsService.getDashboardData());
    }

    @PostMapping("/artifacts")
    public Result<Map<String, Object>> getArtifactStats(@RequestBody StatisticsQueryDTO queryDTO) {
        return Result.success(statisticsService.getArtifactStats(queryDTO));
    }

    @PostMapping("/exhibitions")
    public Result<Map<String, Object>> getExhibitionStats(@RequestBody StatisticsQueryDTO queryDTO) {
        return Result.success(statisticsService.getExhibitionStats(queryDTO));
    }

    @PostMapping("/visitors")
    public Result<Map<String, Object>> getVisitorStats(@RequestBody StatisticsQueryDTO queryDTO) {
        return Result.success(statisticsService.getVisitorStats(queryDTO));
    }

    @PostMapping("/drilldown/artifacts")
    public Result<DrillDownVO> drillDownArtifacts(@RequestBody StatisticsQueryDTO queryDTO) {
        return Result.success(statisticsService.drillDownArtifacts(queryDTO));
    }

    @GetMapping("/ai/analysis")
    public Result<String> getAIAnalysis(@RequestParam String queryType) {
        return Result.success(statisticsService.generateAIAnalysis(queryType));
    }
}
