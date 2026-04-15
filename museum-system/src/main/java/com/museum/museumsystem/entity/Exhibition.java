package com.museum.museumsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("exhibition")
public class Exhibition {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String theme;
    private LocalDate startDate;
    private LocalDate endDate;
    private String venue;
    private String curator;
    private String description;
    private String posterUrl;
    private String status; // PLANNING, ONGOING, FINISHED
    private Long createdBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}