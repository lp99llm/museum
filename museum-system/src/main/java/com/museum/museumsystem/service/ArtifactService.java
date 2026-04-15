package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.entity.Artifact;
import com.museum.museumsystem.dto.request.ArtifactQueryDTO;
import com.museum.museumsystem.common.PageResult;

public interface ArtifactService extends IService<Artifact> {
    PageResult<Artifact> pageQuery(ArtifactQueryDTO queryDTO);
    Artifact getByCode(String code);
    boolean updateState(Long id, String targetState, String remark);
}