package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.WarehousingQueryDTO;
import com.museum.museumsystem.entity.Warehousing;

public interface WarehousingService extends IService<Warehousing> {
    PageResult<Warehousing> pageQuery(WarehousingQueryDTO queryDTO);
}
