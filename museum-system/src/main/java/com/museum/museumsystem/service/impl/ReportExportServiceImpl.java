package com.museum.museumsystem.service.impl;

import com.museum.museumsystem.dto.request.StatisticsQueryDTO;
import com.museum.museumsystem.dto.response.DrillDownVO;
import com.museum.museumsystem.service.ReportExportService;
import com.museum.museumsystem.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportExportServiceImpl implements ReportExportService {

    private final StatisticsService statisticsService;

    @Override
    public void exportToExcel(String type, Map<String, Object> params, HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        String fileName = URLEncoder.encode(getFileName(type), "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(getSheetName(type));

        switch (type) {
            case "artifact_drilldown":
                exportArtifactDrillDown(sheet, params);
                break;
            case "artifact_stats":
                exportArtifactStats(sheet, params);
                break;
            case "exhibition_stats":
                exportExhibitionStats(sheet, params);
                break;
            default:
                exportDefault(sheet);
        }

        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        workbook.close();
    }

    @Override
    public void exportToPdf(String type, Map<String, Object> params, HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setCharacterEncoding("utf-8");

        String fileName = URLEncoder.encode(getFileName(type) + ".pdf", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(getSheetName(type));

        switch (type) {
            case "artifact_drilldown":
                exportArtifactDrillDown(sheet, params);
                break;
            default:
                exportDefault(sheet);
        }

        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        workbook.close();
    }

    private void exportArtifactDrillDown(Sheet sheet, Map<String, Object> params) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"编号", "名称", "类别", "年代", "材质", "保存状态", "创建时间"};

        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        Font headerFont = sheet.getWorkbook().createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 4000);
        }

        StatisticsQueryDTO queryDTO = new StatisticsQueryDTO();
        DrillDownVO drillDown = statisticsService.drillDownArtifacts(queryDTO);

        List<Map<String, Object>> records = drillDown.getRecords();
        if (records != null) {
            int rowNum = 1;
            for (Map<String, Object> record : records) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(record.get("编号") != null ? record.get("编号").toString() : "");
                row.createCell(1).setCellValue(record.get("名称") != null ? record.get("名称").toString() : "");
                row.createCell(2).setCellValue(record.get("类别") != null ? record.get("类别").toString() : "");
                row.createCell(3).setCellValue(record.get("年代") != null ? record.get("年代").toString() : "");
                row.createCell(4).setCellValue(record.get("材质") != null ? record.get("材质").toString() : "");
                row.createCell(5).setCellValue(record.get("保存状态") != null ? record.get("保存状态").toString() : "");
                row.createCell(6).setCellValue(record.get("创建时间") != null ? record.get("创建时间").toString() : "");
            }
        }
    }

    private void exportArtifactStats(Sheet sheet, Map<String, Object> params) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"类别", "数量", "占比(%)"};

        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        Font headerFont = sheet.getWorkbook().createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 4000);
        }

        List<Map<String, Object>> categoryDist = (List<Map<String, Object>>) params.get("categoryDistribution");
        if (categoryDist != null) {
            int rowNum = 1;
            for (Map<String, Object> item : categoryDist) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.get("category") != null ? item.get("category").toString() : "");
                row.createCell(1).setCellValue(item.get("count") != null ? item.get("count").toString() : "0");
                row.createCell(2).setCellValue(item.get("percentage") != null ? item.get("percentage").toString() : "0");
            }
        }
    }

    private void exportExhibitionStats(Sheet sheet, Map<String, Object> params) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"展览名称", "参观人数", "观众评分"};

        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        Font headerFont = sheet.getWorkbook().createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 6000);
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> effects = (List<Map<String, Object>>) params.get("exhibitionEffects");
        if (effects != null) {
            int rowNum = 1;
            for (Map<String, Object> item : effects) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.get("exhibitionName") != null ? item.get("exhibitionName").toString() : "");
                row.createCell(1).setCellValue(item.get("visitorCount") != null ? item.get("visitorCount").toString() : "0");
                row.createCell(2).setCellValue(item.get("feedbackScore") != null ? item.get("feedbackScore").toString() : "0");
            }
        }
    }

    private void exportDefault(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        Cell cell = headerRow.createCell(0);
        cell.setCellValue("数据报表");
    }

    private String getFileName(String type) {
        switch (type) {
            case "artifact_drilldown":
                return "文物明细报表.xlsx";
            case "artifact_stats":
                return "文物统计报表.xlsx";
            case "exhibition_stats":
                return "展览统计报表.xlsx";
            default:
                return "数据报表.xlsx";
        }
    }

    private String getSheetName(String type) {
        switch (type) {
            case "artifact_drilldown":
                return "文物明细";
            case "artifact_stats":
                return "文物统计";
            case "exhibition_stats":
                return "展览统计";
            default:
                return "数据";
        }
    }
}
