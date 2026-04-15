package com.museum.museumsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("warehouse_in")
public class WarehouseIn {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long artifactId;
    private LocalDateTime inDate;
    private String location;
    private Long operator;
    private String remarks;
    private LocalDateTime createdTime;
}