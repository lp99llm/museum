package com.museum.museumsystem.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("disposal_flow")
public class DisposalFlow {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long disposalId;

    private String stageName;

    private String approverName;

    private String approverRole;

    private String approvalOpinion;

    private String status;

    private LocalDateTime approveTime;

    private String remarks;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
