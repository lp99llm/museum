package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.StorageCheckQueryDTO;
import com.museum.museumsystem.entity.StorageCheck;

public interface StorageCheckService extends IService<StorageCheck> {
    PageResult<StorageCheck> pageQuery(StorageCheckQueryDTO queryDTO);
}