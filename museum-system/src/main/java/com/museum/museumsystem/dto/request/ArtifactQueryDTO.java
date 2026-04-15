package com.museum.museumsystem.dto.request;

import lombok.Data;

@Data
public class ArtifactQueryDTO {
    private String name;
    private String category;
    private String currentState;
    private Long current = 1L;
    private Long size = 10L;
}