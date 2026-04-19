package com.museum.museumsystem.dto.request;

import lombok.Data;

@Data
public class ArtifactQueryDTO {
    private long current = 1;
    private long size = 10;
    private String code;
    private String name;
    private String category;
    private String era;
    private String material;
    private String source;
    private String preservationStatus;
    private String appraisalLevel;
    private String currentState;
    private String location;
}
