package com.museum.museumsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("exhibition_artifact")
public class ExhibitionArtifact {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long exhibitionId;
    private Long artifactId;
    private Integer displayOrder;
    private String remark;
}