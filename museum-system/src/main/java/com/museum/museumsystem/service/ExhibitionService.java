package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.ExhibitionQueryDTO;
import com.museum.museumsystem.entity.Exhibition;
import com.museum.museumsystem.entity.ExhibitionArtifact;

import java.util.List;

public interface ExhibitionService extends IService<Exhibition> {
    PageResult<Exhibition> pageQuery(ExhibitionQueryDTO queryDTO);

    // 为展览添加文物
    boolean addArtifactToExhibition(Long exhibitionId, Long artifactId, Integer displayOrder, String remark);

    // 从展览移除文物
    boolean removeArtifactFromExhibition(Long exhibitionId, Long artifactId);

    // 查询展览的所有文物ID
    List<Long> getArtifactIdsByExhibitionId(Long exhibitionId);
}