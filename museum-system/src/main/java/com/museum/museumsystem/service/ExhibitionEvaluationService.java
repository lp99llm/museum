package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.ExhibitionEvaluationDTO;
import com.museum.museumsystem.dto.request.ExhibitionEvaluationQueryDTO;
import com.museum.museumsystem.entity.ExhibitionEvaluation;

public interface ExhibitionEvaluationService extends IService<ExhibitionEvaluation> {
    PageResult<ExhibitionEvaluationDTO> pageQuery(ExhibitionEvaluationQueryDTO queryDTO);
}
