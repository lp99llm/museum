package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.dto.request.ReportScheduleRequest;
import com.museum.museumsystem.dto.request.StatisticsQueryDTO;
import com.museum.museumsystem.dto.response.DrillDownVO;
import com.museum.museumsystem.service.ReportExportService;
import com.museum.museumsystem.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportExportService reportExportService;
    private final StatisticsService statisticsService;

    @PostMapping("/export/artifacts/excel")
    @PreAuthorize("hasAuthority('report:export')")
    public void exportArtifactExcel(@RequestBody StatisticsQueryDTO queryDTO, HttpServletResponse response) throws Exception {
        Map<String, Object> params = new HashMap<>();
        DrillDownVO drillDown = statisticsService.drillDownArtifacts(queryDTO);
        params.put("records", drillDown.getRecords());
        reportExportService.exportToExcel("artifact_drilldown", params, response);
    }

    @PostMapping("/export/artifacts/stats/excel")
    @PreAuthorize("hasAuthority('report:export')")
    public void exportArtifactStatsExcel(@RequestBody(required = false) StatisticsQueryDTO queryDTO, HttpServletResponse response) throws Exception {
        if (queryDTO == null) queryDTO = new StatisticsQueryDTO();
        Map<String, Object> params = statisticsService.getArtifactStats(queryDTO);
        reportExportService.exportToExcel("artifact_stats", params, response);
    }

    @PostMapping("/export/exhibitions/stats/excel")
    @PreAuthorize("hasAuthority('report:export')")
    public void exportExhibitionStatsExcel(@RequestBody(required = false) StatisticsQueryDTO queryDTO, HttpServletResponse response) throws Exception {
        if (queryDTO == null) queryDTO = new StatisticsQueryDTO();
        Map<String, Object> params = statisticsService.getExhibitionStats(queryDTO);
        reportExportService.exportToExcel("exhibition_stats", params, response);
    }

    @PostMapping("/export/artifacts/pdf")
    @PreAuthorize("hasAuthority('report:export')")
    public void exportArtifactPdf(@RequestBody StatisticsQueryDTO queryDTO, HttpServletResponse response) throws Exception {
        Map<String, Object> params = new HashMap<>();
        DrillDownVO drillDown = statisticsService.drillDownArtifacts(queryDTO);
        params.put("records", drillDown.getRecords());
        reportExportService.exportToPdf("artifact_drilldown", params, response);
    }

    @PostMapping("/schedule")
    @PreAuthorize("hasAuthority('report:export')")
    public Result<Map<String, Object>> schedule(@RequestBody @Valid ReportScheduleRequest request) {
        return Result.success(statisticsService.scheduleReport(request.getName(), request.getCron(), request.getMetrics(), request.getReceiver()));
    }
}
