package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.entity.Role;

public interface RoleService extends IService<Role> {
    // 新增分页查询方法
    PageResult<Role> pageQuery(Long current, Long size, String roleName);
}