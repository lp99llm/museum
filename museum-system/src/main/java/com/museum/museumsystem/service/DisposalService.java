package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.DisposalQueryDTO;
import com.museum.museumsystem.entity.Disposal;

public interface DisposalService extends IService<Disposal> {
    PageResult<Disposal> pageQuery(DisposalQueryDTO queryDTO);
}