package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.entity.Artifact;
import com.museum.museumsystem.mapper.ArtifactMapper;
import com.museum.museumsystem.service.ArtifactService;
import com.museum.museumsystem.dto.request.ArtifactQueryDTO;
import com.museum.museumsystem.common.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ArtifactServiceImpl extends ServiceImpl<ArtifactMapper, Artifact> implements ArtifactService {

    @Override
    public PageResult<Artifact> pageQuery(ArtifactQueryDTO queryDTO) {
        Page<Artifact> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());

        LambdaQueryWrapper<Artifact> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getCode())) {
            wrapper.eq(Artifact::getCode, queryDTO.getCode());
        }
        if (StringUtils.hasText(queryDTO.getName())) {
            wrapper.like(Artifact::getName, queryDTO.getName());
        }
        if (StringUtils.hasText(queryDTO.getCategory())) {
            wrapper.eq(Artifact::getCategory, queryDTO.getCategory());
        }
        if (StringUtils.hasText(queryDTO.getEra())) {
            wrapper.eq(Artifact::getEra, queryDTO.getEra());
        }
        if (StringUtils.hasText(queryDTO.getMaterial())) {
            wrapper.eq(Artifact::getMaterial, queryDTO.getMaterial());
        }
        if (StringUtils.hasText(queryDTO.getSource())) {
            wrapper.eq(Artifact::getSource, queryDTO.getSource());
        }
        if (StringUtils.hasText(queryDTO.getPreservationStatus())) {
            wrapper.eq(Artifact::getPreservationStatus, queryDTO.getPreservationStatus());
        }
        if (StringUtils.hasText(queryDTO.getAppraisalLevel())) {
            wrapper.eq(Artifact::getAppraisalLevel, queryDTO.getAppraisalLevel());
        }
        if (StringUtils.hasText(queryDTO.getCurrentState())) {
            wrapper.eq(Artifact::getCurrentState, queryDTO.getCurrentState());
        }
        if (StringUtils.hasText(queryDTO.getLocation())) {
            wrapper.eq(Artifact::getLocation, queryDTO.getLocation());
        }

        Page<Artifact> result = this.page(page, wrapper);
        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(), result.getPages(), result.getRecords());
    }

    @Override
    public Artifact getByCode(String code) {
        LambdaQueryWrapper<Artifact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Artifact::getCode, code);
        return this.getOne(wrapper);
    }

    @Override
    public boolean updateState(Long id, String targetState, String remark) {
        Artifact artifact = this.getById(id);
        if (artifact == null) {
            return false;
        }
        artifact.setCurrentState(targetState);
        return this.updateById(artifact);
    }
}
