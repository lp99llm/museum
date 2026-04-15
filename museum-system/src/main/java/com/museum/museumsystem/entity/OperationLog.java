package com.museum.museumsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("operation_log")
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long operator;
    private String operationType; // ADD, UPDATE, DELETE, LOGIN, EXPORT
    private String operationModule;
    private String operationDesc;
    private String requestIp;
    private String requestUrl;
    private String requestParams;
    private String result; // SUCCESS, FAIL
    private String errorMsg;
    private Long duration;
    private LocalDateTime operationTime;
}