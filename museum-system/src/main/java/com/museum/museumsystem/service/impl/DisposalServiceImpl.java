package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.DisposalQueryDTO;
import com.museum.museumsystem.entity.Disposal;
import com.museum.museumsystem.mapper.DisposalMapper;
import com.museum.museumsystem.service.DisposalService;
import org.springframework.stereotype.Service;

@Service
public class DisposalServiceImpl extends ServiceImpl<DisposalMapper, Disposal> implements DisposalService {

    @Override
    public PageResult<Disposal> pageQuery(DisposalQueryDTO queryDTO) {
        Page<Disposal> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        LambdaQueryWrapper<Disposal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getArtifactId() != null, Disposal::getArtifactId, queryDTO.getArtifactId())
                .eq(queryDTO.getDisposalType() != null, Disposal::getDisposalType, queryDTO.getDisposalType())
                .orderByDesc(Disposal::getCreatedTime);
        Page<Disposal> pageResult = this.page(page, wrapper);
        PageResult<Disposal> result = new PageResult<>();
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setPages(pageResult.getPages());
        result.setRecords(pageResult.getRecords());
        return result;
    }
}