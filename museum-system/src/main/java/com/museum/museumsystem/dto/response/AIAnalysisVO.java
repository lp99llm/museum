package com.museum.museumsystem.dto.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AIAnalysisVO {
    private String queryType;
    private String title;
    private String summary;
    private String trend;
    private List<String> suggestions;
    private List<Map<String, Object>> cards;
}
