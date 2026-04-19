package com.museum.museumsystem.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("visitor_appointment")
public class VisitorAppointment {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long exhibitionId;

    private String visitorName;

    private String visitorPhone;

    private String visitorIdCard;

    private LocalDate appointmentDate;

    private String appointmentTimeSlot;

    private Integer visitorCount;

    private String status;

    private String remark;

    private Long createdBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
