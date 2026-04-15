package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.ExhibitionQueryDTO;
import com.museum.museumsystem.entity.Exhibition;
import com.museum.museumsystem.entity.ExhibitionArtifact;
import com.museum.museumsystem.mapper.ExhibitionMapper;
import com.museum.museumsystem.service.ExhibitionArtifactService;
import com.museum.museumsystem.service.ExhibitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExhibitionServiceImpl extends ServiceImpl<ExhibitionMapper, Exhibition> implements ExhibitionService {

    private final ExhibitionArtifactService exhibitionArtifactService;

    @Override
    public PageResult<Exhibition> pageQuery(ExhibitionQueryDTO queryDTO) {
        Page<Exhibition> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        LambdaQueryWrapper<Exhibition> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getName()), Exhibition::getName, queryDTO.getName())
                .eq(StringUtils.hasText(queryDTO.getStatus()), Exhibition::getStatus, queryDTO.getStatus())
                .orderByDesc(Exhibition::getCreatedTime);

        Page<Exhibition> pageResult = this.page(page, wrapper);
        PageResult<Exhibition> result = new PageResult<>();
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setPages(pageResult.getPages());
        result.setRecords(pageResult.getRecords());
        return result;
    }

    @Override
    @Transactional
    public boolean addArtifactToExhibition(Long exhibitionId, Long artifactId, Integer displayOrder, String remark) {
        // 检查展览是否存在（业务异常可在Controller层处理）
        ExhibitionArtifact relation = new ExhibitionArtifact();
        relation.setExhibitionId(exhibitionId);
        relation.setArtifactId(artifactId);
        relation.setDisplayOrder(displayOrder);
        relation.setRemark(remark);
        return exhibitionArtifactService.save(relation);
    }

    @Override
    @Transactional
    public boolean removeArtifactFromExhibition(Long exhibitionId, Long artifactId) {
        LambdaQueryWrapper<ExhibitionArtifact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExhibitionArtifact::getExhibitionId, exhibitionId)
                .eq(ExhibitionArtifact::getArtifactId, artifactId);
        return exhibitionArtifactService.remove(wrapper);
    }

    @Override
    public List<Long> getArtifactIdsByExhibitionId(Long exhibitionId) {
        LambdaQueryWrapper<ExhibitionArtifact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExhibitionArtifact::getExhibitionId, exhibitionId);
        return exhibitionArtifactService.list(wrapper).stream()
                .map(ExhibitionArtifact::getArtifactId)
                .collect(Collectors.toList());
    }
}