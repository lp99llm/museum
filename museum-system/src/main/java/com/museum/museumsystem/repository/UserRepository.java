package com.museum.museumsystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.museum.museumsystem.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRepository extends BaseMapper<User> {
}