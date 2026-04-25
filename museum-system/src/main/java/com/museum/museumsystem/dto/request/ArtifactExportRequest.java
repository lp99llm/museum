package com.museum.museumsystem.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ArtifactExportRequest {
    private List<Long> ids;
    private String code;
    private String name;
    private String keyword;
    private String category;
    private String era;
    private String material;
    private String source;
    private String preservationStatus;
    private String appraisalLevel;
    private String currentState;
    private String location;
}
