package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.WarehouseInQueryDTO;
import com.museum.museumsystem.entity.WarehouseIn;

public interface WarehouseInService extends IService<WarehouseIn> {
    PageResult<WarehouseIn> pageQuery(WarehouseInQueryDTO queryDTO);
}