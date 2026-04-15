package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.AcquisitionQueryDTO;
import com.museum.museumsystem.entity.Acquisition;
import com.museum.museumsystem.mapper.AcquisitionMapper;
import com.museum.museumsystem.service.AcquisitionService;
import org.springframework.stereotype.Service;

@Service
public class AcquisitionServiceImpl extends ServiceImpl<AcquisitionMapper, Acquisition> implements AcquisitionService {

    @Override
    public PageResult<Acquisition> pageQuery(AcquisitionQueryDTO queryDTO) {
        Page<Acquisition> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        LambdaQueryWrapper<Acquisition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getArtifactId() != null, Acquisition::getArtifactId, queryDTO.getArtifactId())
                .eq(queryDTO.getAcquisitionType() != null, Acquisition::getAcquisitionType, queryDTO.getAcquisitionType())
                .orderByDesc(Acquisition::getCreatedTime);
        Page<Acquisition> pageResult = this.page(page, wrapper);
        PageResult<Acquisition> result = new PageResult<>();
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setPages(pageResult.getPages());
        result.setRecords(pageResult.getRecords());
        return result;
    }
}