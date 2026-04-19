package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.ExhibitionArtifactDTO;
import com.museum.museumsystem.dto.request.ExhibitionQueryDTO;
import com.museum.museumsystem.entity.Artifact;
import com.museum.museumsystem.entity.Exhibition;
import com.museum.museumsystem.entity.ExhibitionArtifact;
import com.museum.museumsystem.mapper.ArtifactMapper;
import com.museum.museumsystem.mapper.ExhibitionMapper;
import com.museum.museumsystem.service.ExhibitionArtifactService;
import com.museum.museumsystem.service.ExhibitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExhibitionServiceImpl extends ServiceImpl<ExhibitionMapper, Exhibition> implements ExhibitionService {

    private final ExhibitionArtifactService exhibitionArtifactService;
    private final ArtifactMapper artifactMapper;

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

    @Override
    public List<ExhibitionArtifactDTO> getArtifactDetailsByExhibitionId(Long exhibitionId) {
        LambdaQueryWrapper<ExhibitionArtifact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExhibitionArtifact::getExhibitionId, exhibitionId)
                .orderByAsc(ExhibitionArtifact::getDisplayOrder);
        List<ExhibitionArtifact> relations = exhibitionArtifactService.list(wrapper);

        if (relations.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> artifactIds = relations.stream()
                .map(ExhibitionArtifact::getArtifactId)
                .collect(Collectors.toList());

        List<Artifact> artifacts = artifactMapper.selectBatchIds(artifactIds);
        Map<Long, Artifact> artifactMap = artifacts.stream()
                .collect(Collectors.toMap(Artifact::getId, a -> a));

        return relations.stream().map(rel -> {
            ExhibitionArtifactDTO dto = new ExhibitionArtifactDTO();
            dto.setArtifactId(rel.getArtifactId());
            dto.setDisplayOrder(rel.getDisplayOrder());
            dto.setRemark(rel.getRemark());

            Artifact artifact = artifactMap.get(rel.getArtifactId());
            if (artifact != null) {
                dto.setCode(artifact.getCode());
                dto.setName(artifact.getName());
                dto.setCategory(artifact.getCategory());
                dto.setEra(artifact.getEra());
                dto.setMaterial(artifact.getMaterial());
            }
            return dto;
        }).collect(Collectors.toList());
    }
}
