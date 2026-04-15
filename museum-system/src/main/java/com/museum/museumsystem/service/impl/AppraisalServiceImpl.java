package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.AppraisalQueryDTO;
import com.museum.museumsystem.entity.Appraisal;
import com.museum.museumsystem.mapper.AppraisalMapper;
import com.museum.museumsystem.service.AppraisalService;
import org.springframework.stereotype.Service;

@Service
public class AppraisalServiceImpl extends ServiceImpl<AppraisalMapper, Appraisal> implements AppraisalService {

    @Override
    public PageResult<Appraisal> pageQuery(AppraisalQueryDTO queryDTO) {
        Page<Appraisal> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        LambdaQueryWrapper<Appraisal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getArtifactId() != null, Appraisal::getArtifactId, queryDTO.getArtifactId())
                .eq(queryDTO.getAppraisalLevel() != null, Appraisal::getAppraisalLevel, queryDTO.getAppraisalLevel())
                .orderByDesc(Appraisal::getCreatedTime);
        Page<Appraisal> pageResult = this.page(page, wrapper);
        PageResult<Appraisal> result = new PageResult<>();
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setPages(pageResult.getPages());
        result.setRecords(pageResult.getRecords());
        return result;
    }
}