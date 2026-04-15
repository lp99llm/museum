package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.entity.User;

public interface UserService extends IService<User> {
    User getByUsername(String username);

    // 新增分页查询方法
    PageResult<User> pageQuery(Long current, Long size, String username);
}