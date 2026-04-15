package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.RestorationQueryDTO;
import com.museum.museumsystem.entity.Restoration;

public interface RestorationService extends IService<Restoration> {
    PageResult<Restoration> pageQuery(RestorationQueryDTO queryDTO);
}