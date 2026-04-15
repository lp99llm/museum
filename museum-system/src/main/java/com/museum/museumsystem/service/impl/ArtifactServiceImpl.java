package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.common.BusinessException;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.ArtifactQueryDTO;
import com.museum.museumsystem.entity.Artifact;
import com.museum.museumsystem.entity.ArtifactStateLog;
import com.museum.museumsystem.mapper.ArtifactMapper;
import com.museum.museumsystem.service.ArtifactService;
import com.museum.museumsystem.service.ArtifactStateLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ArtifactServiceImpl extends ServiceImpl<ArtifactMapper, Artifact> implements ArtifactService {

    private final ArtifactStateLogService stateLogService; // 稍后创建

    @Override
    public PageResult<Artifact> pageQuery(ArtifactQueryDTO queryDTO) {
        Page<Artifact> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        LambdaQueryWrapper<Artifact> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getName()), Artifact::getName, queryDTO.getName())
                .eq(StringUtils.hasText(queryDTO.getCategory()), Artifact::getCategory, queryDTO.getCategory())
                .eq(StringUtils.hasText(queryDTO.getCurrentState()), Artifact::getCurrentState, queryDTO.getCurrentState())
                .orderByDesc(Artifact::getCreatedTime);

        Page<Artifact> pageResult = this.page(page, wrapper);

        PageResult<Artifact> result = new PageResult<>();
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setPages(pageResult.getPages());
        result.setRecords(pageResult.getRecords());
        return result;
    }

    @Override
    public Artifact getByCode(String code) {
        LambdaQueryWrapper<Artifact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Artifact::getCode, code);
        return this.getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateState(Long id, String state, Long operatorId, String remark) {
        Artifact artifact = this.getById(id);
        if (artifact == null) {
            throw new BusinessException("文物不存在");
        }
        String oldState = artifact.getCurrentState();
        artifact.setCurrentState(state);
        boolean updated = this.updateById(artifact);
        if (updated) {
            ArtifactStateLog log = new ArtifactStateLog();
            log.setArtifactId(id);
            log.setFromState(oldState);
            log.setToState(state);
            log.setOperator(operatorId);
            log.setRemark(remark);
            stateLogService.save(log);
        }
        return updated;
    }
}