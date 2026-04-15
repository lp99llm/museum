package com.museum.museumsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.museum.museumsystem.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM user WHERE username = #{username} AND deleted = 0")
    User selectByUsername(String username);
}