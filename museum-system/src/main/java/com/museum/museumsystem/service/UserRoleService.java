package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.entity.UserRole;

import java.util.List;

public interface UserRoleService extends IService<UserRole> {
    // 根据用户ID查询角色ID列表
    List<Long> getRoleIdsByUserId(Long userId);

    // 给用户分配角色（先删除原有，再新增）
    boolean assignRolesToUser(Long userId, List<Long> roleIds);
}