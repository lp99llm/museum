package com.museum.museumsystem.dto.request;

import lombok.Data;

@Data
public class ExhibitionEvaluationQueryDTO {
    private Long current = 1L;
    private Long size = 10L;
    private Long exhibitionId;
}
