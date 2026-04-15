package com.museum.museumsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.museum.museumsystem.entity.Exhibition;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExhibitionMapper extends BaseMapper<Exhibition> {
}