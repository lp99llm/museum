package com.museum.museumsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.museum.museumsystem.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}