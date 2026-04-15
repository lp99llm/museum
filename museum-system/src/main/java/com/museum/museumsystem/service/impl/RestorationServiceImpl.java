package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.RestorationQueryDTO;
import com.museum.museumsystem.entity.Restoration;
import com.museum.museumsystem.mapper.RestorationMapper;
import com.museum.museumsystem.service.RestorationService;
import org.springframework.stereotype.Service;

@Service
public class RestorationServiceImpl extends ServiceImpl<RestorationMapper, Restoration> implements RestorationService {

    @Override
    public PageResult<Restoration> pageQuery(RestorationQueryDTO queryDTO) {
        Page<Restoration> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        LambdaQueryWrapper<Restoration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getArtifactId() != null, Restoration::getArtifactId, queryDTO.getArtifactId())
                .eq(queryDTO.getRestorer() != null, Restoration::getRestorer, queryDTO.getRestorer())
                .orderByDesc(Restoration::getCreatedTime);
        Page<Restoration> pageResult = this.page(page, wrapper);
        PageResult<Restoration> result = new PageResult<>();
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setPages(pageResult.getPages());
        result.setRecords(pageResult.getRecords());
        return result;
    }
}