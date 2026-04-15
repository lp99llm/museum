package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.AppraisalQueryDTO;
import com.museum.museumsystem.entity.Appraisal;

public interface AppraisalService extends IService<Appraisal> {
    PageResult<Appraisal> pageQuery(AppraisalQueryDTO queryDTO);
}