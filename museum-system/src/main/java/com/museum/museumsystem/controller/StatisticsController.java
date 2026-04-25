package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.dto.request.ReportScheduleRequest;
import com.museum.museumsystem.dto.request.StatisticsQueryDTO;
import com.museum.museumsystem.dto.response.AIAnalysisVO;
import com.museum.museumsystem.dto.response.DashboardVO;
import com.museum.museumsystem.dto.response.DrillDownVO;
import com.museum.museumsystem.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import javax.validation.Valid;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('statistics:view')")
    public Result<DashboardVO> getDashboard() {
        return Result.success(statisticsService.getDashboardData());
    }

    @PostMapping("/artifacts")
    @PreAuthorize("hasAuthority('statistics:view')")
    public Result<Map<String, Object>> getArtifactStats(@RequestBody StatisticsQueryDTO queryDTO) {
        return Result.success(statisticsService.getArtifactStats(queryDTO));
    }

    @PostMapping("/exhibitions")
    @PreAuthorize("hasAuthority('statistics:view')")
    public Result<Map<String, Object>> getExhibitionStats(@RequestBody StatisticsQueryDTO queryDTO) {
        return Result.success(statisticsService.getExhibitionStats(queryDTO));
    }

    @PostMapping("/visitors")
    @PreAuthorize("hasAuthority('statistics:view')")
    public Result<Map<String, Object>> getVisitorStats(@RequestBody StatisticsQueryDTO queryDTO) {
        return Result.success(statisticsService.getVisitorStats(queryDTO));
    }

    @PostMapping("/process")
    @PreAuthorize("hasAuthority('statistics:view')")
    public Result<Map<String, Object>> getProcessStats(@RequestBody StatisticsQueryDTO queryDTO) {
        return Result.success(statisticsService.getProcessStats(queryDTO));
    }

    @GetMapping("/space-heatmap")
    @PreAuthorize("hasAuthority('statistics:view')")
    public Result<Map<String, Object>> getSpaceHeatmapStats() {
        return Result.success(statisticsService.getSpaceHeatmapStats());
    }

    @PostMapping("/drilldown/artifacts")
    @PreAuthorize("hasAuthority('statistics:view')")
    public Result<DrillDownVO> drillDownArtifacts(@RequestBody StatisticsQueryDTO queryDTO) {
        return Result.success(statisticsService.drillDownArtifacts(queryDTO));
    }

    @GetMapping("/ai/analysis")
    @PreAuthorize("hasAuthority('statistics:view')")
    public Result<AIAnalysisVO> getAIAnalysis(@RequestParam String queryType) {
        return Result.success(statisticsService.getAIAnalysis(queryType));
    }

    @GetMapping("/exhibition-performance")
    @PreAuthorize("hasAuthority('statistics:view')")
    public Result<Map<String, Object>> getExhibitionPerformance() {
        return Result.success(statisticsService.getExhibitionPerformance());
    }

    @GetMapping("/process-efficiency/detail")
    @PreAuthorize("hasAuthority('statistics:view')")
    public Result<Map<String, Object>> getProcessEfficiencyDetail() {
        return Result.success(statisticsService.getProcessEfficiencyDetail());
    }

    @PostMapping("/report/schedule")
    @PreAuthorize("hasAuthority('report:export')")
    public Result<Map<String, Object>> scheduleReport(@RequestBody @Valid ReportScheduleRequest request) {
        return Result.success(statisticsService.scheduleReport(request.getName(), request.getCron(), request.getMetrics(), request.getReceiver()));
    }
}
