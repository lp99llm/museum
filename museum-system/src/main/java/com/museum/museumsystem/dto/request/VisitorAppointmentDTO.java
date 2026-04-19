package com.museum.museumsystem.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class VisitorAppointmentDTO {
    private Long id;
    private Long exhibitionId;
    private String exhibitionName;
    private String visitorName;
    private String visitorPhone;
    private String visitorIdCard;
    private LocalDate appointmentDate;
    private String appointmentTimeSlot;
    private Integer visitorCount;
    private String status;
    private String remark;
}
