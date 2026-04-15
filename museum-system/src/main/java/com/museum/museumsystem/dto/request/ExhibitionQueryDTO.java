package com.museum.museumsystem.dto.request;

import lombok.Data;

@Data
public class ExhibitionQueryDTO {
    private String name;
    private String status;       // PLANNING, ONGOING, FINISHED
    private Long current = 1L;
    private Long size = 10L;
}