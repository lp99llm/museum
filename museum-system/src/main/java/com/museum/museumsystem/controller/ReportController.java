package com.museum.museumsystem.controller;

import com.museum.museumsystem.dto.request.StatisticsQueryDTO;
import com.museum.museumsystem.dto.response.DrillDownVO;
import com.museum.museumsystem.service.ReportExportService;
import com.museum.museumsystem.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportExportService reportExportService;
    private final StatisticsService statisticsService;

    @PostMapping("/export/artifacts/excel")
    public void exportArtifactExcel(@RequestBody StatisticsQueryDTO queryDTO, HttpServletResponse response) throws Exception {
        Map<String, Object> params = new HashMap<>();
        DrillDownVO drillDown = statisticsService.drillDownArtifacts(queryDTO);
        params.put("records", drillDown.getRecords());
        reportExportService.exportToExcel("artifact_drilldown", params, response);
    }

    @PostMapping("/export/artifacts/stats/excel")
    public void exportArtifactStatsExcel(@RequestBody(required = false) StatisticsQueryDTO queryDTO, HttpServletResponse response) throws Exception {
        if (queryDTO == null) queryDTO = new StatisticsQueryDTO();
        Map<String, Object> params = statisticsService.getArtifactStats(queryDTO);
        reportExportService.exportToExcel("artifact_stats", params, response);
    }

    @PostMapping("/export/exhibitions/stats/excel")
    public void exportExhibitionStatsExcel(@RequestBody(required = false) StatisticsQueryDTO queryDTO, HttpServletResponse response) throws Exception {
        if (queryDTO == null) queryDTO = new StatisticsQueryDTO();
        Map<String, Object> params = statisticsService.getExhibitionStats(queryDTO);
        reportExportService.exportToExcel("exhibition_stats", params, response);
    }

    @PostMapping("/export/artifacts/pdf")
    public void exportArtifactPdf(@RequestBody StatisticsQueryDTO queryDTO, HttpServletResponse response) throws Exception {
        Map<String, Object> params = new HashMap<>();
        DrillDownVO drillDown = statisticsService.drillDownArtifacts(queryDTO);
        params.put("records", drillDown.getRecords());
        reportExportService.exportToPdf("artifact_drilldown", params, response);
    }
}
