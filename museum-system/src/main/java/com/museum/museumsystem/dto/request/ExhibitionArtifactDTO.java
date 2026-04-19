package com.museum.museumsystem.dto.request;

import lombok.Data;

@Data
public class ExhibitionArtifactDTO {
    private Long artifactId;
    private String code;
    private String name;
    private String category;
    private String era;
    private String material;
    private Integer displayOrder;
    private String remark;
}
