package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.ExhibitionArtifactDTO;
import com.museum.museumsystem.dto.request.ExhibitionQueryDTO;
import com.museum.museumsystem.entity.Exhibition;
import com.museum.museumsystem.entity.ExhibitionArtifact;

import java.util.List;

public interface ExhibitionService extends IService<Exhibition> {
    PageResult<Exhibition> pageQuery(ExhibitionQueryDTO queryDTO);

    boolean addArtifactToExhibition(Long exhibitionId, Long artifactId, Integer displayOrder, String remark);

    boolean removeArtifactFromExhibition(Long exhibitionId, Long artifactId);

    List<Long> getArtifactIdsByExhibitionId(Long exhibitionId);

    List<ExhibitionArtifactDTO> getArtifactDetailsByExhibitionId(Long exhibitionId);
}
