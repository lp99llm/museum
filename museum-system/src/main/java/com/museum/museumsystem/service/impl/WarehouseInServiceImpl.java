package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.WarehouseInQueryDTO;
import com.museum.museumsystem.entity.WarehouseIn;
import com.museum.museumsystem.mapper.WarehouseInMapper;
import com.museum.museumsystem.service.WarehouseInService;
import org.springframework.stereotype.Service;

@Service
public class WarehouseInServiceImpl extends ServiceImpl<WarehouseInMapper, WarehouseIn> implements WarehouseInService {

    @Override
    public PageResult<WarehouseIn> pageQuery(WarehouseInQueryDTO queryDTO) {
        Page<WarehouseIn> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        LambdaQueryWrapper<WarehouseIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getArtifactId() != null, WarehouseIn::getArtifactId, queryDTO.getArtifactId())
                .eq(queryDTO.getOperator() != null, WarehouseIn::getOperator, queryDTO.getOperator())
                .orderByDesc(WarehouseIn::getCreatedTime);
        Page<WarehouseIn> pageResult = this.page(page, wrapper);

        PageResult<WarehouseIn> result = new PageResult<>();
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setPages(pageResult.getPages());
        result.setRecords(pageResult.getRecords());
        return result;
    }
}