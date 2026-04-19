package com.museum.museumsystem.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class VisitorAppointmentQueryDTO {
    private Long current = 1L;
    private Long size = 10L;
    private String visitorName;
    private String visitorPhone;
    private Long exhibitionId;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
}
