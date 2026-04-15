package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.AcquisitionQueryDTO;
import com.museum.museumsystem.entity.Acquisition;

public interface AcquisitionService extends IService<Acquisition> {
    PageResult<Acquisition> pageQuery(AcquisitionQueryDTO queryDTO);
}