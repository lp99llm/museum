package com.museum.museumsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("artifact_state_log")
public class ArtifactStateLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long artifactId;
    private String fromState;
    private String toState;
    private Long operator;
    private LocalDateTime operationTime;
    private String remark;
    private Long bizId;
    private String bizType;
}