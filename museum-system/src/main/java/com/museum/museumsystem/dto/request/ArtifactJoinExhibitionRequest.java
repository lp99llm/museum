package com.museum.museumsystem.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ArtifactJoinExhibitionRequest {
    @NotNull(message = "展览ID不能为空")
    private Long exhibitionId;
    private Integer displayOrder;
    private String remark;
}
