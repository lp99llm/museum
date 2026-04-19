package com.museum.museumsystem.dto.response;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class DrillDownVO {
    private String title;
    private List<String> headers;
    private List<Map<String, Object>> records;
    private Long total;
}
