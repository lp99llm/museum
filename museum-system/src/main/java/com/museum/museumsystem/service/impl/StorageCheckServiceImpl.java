package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.StorageCheckQueryDTO;
import com.museum.museumsystem.entity.StorageCheck;
import com.museum.museumsystem.mapper.StorageCheckMapper;
import com.museum.museumsystem.service.StorageCheckService;
import org.springframework.stereotype.Service;

@Service
public class StorageCheckServiceImpl extends ServiceImpl<StorageCheckMapper, StorageCheck> implements StorageCheckService {

    @Override
    public PageResult<StorageCheck> pageQuery(StorageCheckQueryDTO queryDTO) {
        Page<StorageCheck> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        LambdaQueryWrapper<StorageCheck> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getArtifactId() != null, StorageCheck::getArtifactId, queryDTO.getArtifactId())
                .eq(queryDTO.getCheckResult() != null, StorageCheck::getCheckResult, queryDTO.getCheckResult())
                .eq(queryDTO.getChecker() != null, StorageCheck::getChecker, queryDTO.getChecker())
                .orderByDesc(StorageCheck::getCreatedTime);
        Page<StorageCheck> pageResult = this.page(page, wrapper);
        PageResult<StorageCheck> result = new PageResult<>();
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setPages(pageResult.getPages());
        result.setRecords(pageResult.getRecords());
        return result;
    }
}