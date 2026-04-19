package com.museum.museumsystem.service;

import com.museum.museumsystem.dto.response.DrillDownVO;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface ReportExportService {

    void exportToExcel(String type, Map<String, Object> params, HttpServletResponse response) throws Exception;

    void exportToPdf(String type, Map<String, Object> params, HttpServletResponse response) throws Exception;
}
