package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.ExhibitionEvaluationDTO;
import com.museum.museumsystem.dto.request.ExhibitionEvaluationQueryDTO;
import com.museum.museumsystem.entity.Exhibition;
import com.museum.museumsystem.entity.ExhibitionEvaluation;
import com.museum.museumsystem.mapper.ExhibitionEvaluationMapper;
import com.museum.museumsystem.mapper.ExhibitionMapper;
import com.museum.museumsystem.service.ExhibitionEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExhibitionEvaluationServiceImpl extends ServiceImpl<ExhibitionEvaluationMapper, ExhibitionEvaluation>
        implements ExhibitionEvaluationService {

    private final ExhibitionMapper exhibitionMapper;

    @Override
    public PageResult<ExhibitionEvaluationDTO> pageQuery(ExhibitionEvaluationQueryDTO queryDTO) {
        Page<ExhibitionEvaluation> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        LambdaQueryWrapper<ExhibitionEvaluation> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(queryDTO.getExhibitionId() != null, ExhibitionEvaluation::getExhibitionId, queryDTO.getExhibitionId())
                .orderByDesc(ExhibitionEvaluation::getCreatedTime);

        Page<ExhibitionEvaluation> pageResult = this.page(page, wrapper);
        List<ExhibitionEvaluation> records = pageResult.getRecords();

        List<Long> exhibitionIds = records.stream().map(ExhibitionEvaluation::getExhibitionId).distinct().collect(Collectors.toList());
        Map<Long, String> exhibitionMap = new HashMap<>();
        if (!exhibitionIds.isEmpty()) {
            List<Exhibition> exhibitions = exhibitionMapper.selectBatchIds(exhibitionIds);
            exhibitions.forEach(e -> exhibitionMap.put(e.getId(), e.getName()));
        }

        List<ExhibitionEvaluationDTO> dtoList = records.stream().map(r -> {
            ExhibitionEvaluationDTO dto = new ExhibitionEvaluationDTO();
            dto.setId(r.getId());
            dto.setExhibitionId(r.getExhibitionId());
            dto.setExhibitionName(exhibitionMap.get(r.getExhibitionId()));
            dto.setVisitorCount(r.getVisitorCount());
            dto.setFeedbackScore(r.getFeedbackScore());
            dto.setSafetyIncidents(r.getSafetyIncidents());
            dto.setArtifactDamageCount(r.getArtifactDamageCount());
            dto.setBudgetActual(r.getBudgetActual());
            dto.setRevenue(r.getRevenue());
            dto.setMediaCoverage(r.getMediaCoverage());
            dto.setSocialMediaMentions(r.getSocialMediaMentions());
            dto.setMajorHighlights(r.getMajorHighlights());
            dto.setProblems(r.getProblems());
            dto.setImprovementSuggestions(r.getImprovementSuggestions());
            dto.setEvaluator(r.getEvaluator());
            dto.setEvaluationDate(r.getEvaluationDate());
            return dto;
        }).collect(Collectors.toList());

        PageResult<ExhibitionEvaluationDTO> result = new PageResult<>();
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setPages(pageResult.getPages());
        result.setRecords(dtoList);
        return result;
    }
}
