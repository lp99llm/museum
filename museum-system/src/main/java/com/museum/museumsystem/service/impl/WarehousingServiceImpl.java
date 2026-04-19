package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.WarehousingQueryDTO;
import com.museum.museumsystem.entity.Warehousing;
import com.museum.museumsystem.mapper.WarehousingMapper;
import com.museum.museumsystem.service.WarehousingService;
import org.springframework.stereotype.Service;

@Service
public class WarehousingServiceImpl extends ServiceImpl<WarehousingMapper, Warehousing> implements WarehousingService {

    @Override
    public PageResult<Warehousing> pageQuery(WarehousingQueryDTO queryDTO) {
        Page<Warehousing> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        LambdaQueryWrapper<Warehousing> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(queryDTO.getArtifactId() != null, Warehousing::getArtifactId, queryDTO.getArtifactId())
                .like(queryDTO.getArtifactCode() != null, Warehousing::getArtifactCode, queryDTO.getArtifactCode())
                .like(queryDTO.getArtifactName() != null, Warehousing::getArtifactName, queryDTO.getArtifactName())
                .eq(queryDTO.getStorageArea() != null, Warehousing::getStorageArea, queryDTO.getStorageArea())
                .eq(queryDTO.getStorageStatus() != null, Warehousing::getStorageStatus, queryDTO.getStorageStatus())
                .ge(queryDTO.getStartDate() != null, Warehousing::getStorageDate, queryDTO.getStartDate())
                .le(queryDTO.getEndDate() != null, Warehousing::getStorageDate, queryDTO.getEndDate())
                .orderByDesc(Warehousing::getCreatedTime);

        Page<Warehousing> pageResult = this.page(page, wrapper);
        PageResult<Warehousing> result = new PageResult<>();
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setPages(pageResult.getPages());
        result.setRecords(pageResult.getRecords());
        return result;
    }
}
