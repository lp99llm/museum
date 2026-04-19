package com.museum.museumsystem.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class StatisticsQueryDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private String category;
    private String status;
    private Long exhibitionId;
    private String channel;
}
