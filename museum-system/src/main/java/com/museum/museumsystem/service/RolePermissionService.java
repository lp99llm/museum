package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.entity.RolePermission;

import java.util.List;

public interface RolePermissionService extends IService<RolePermission> {
    // 根据角色ID查询权限ID列表
    List<Long> getPermissionIdsByRoleId(Long roleId);

    // 给角色分配权限（先删除原有，再新增）
    boolean assignPermissionsToRole(Long roleId, List<Long> permissionIds);
}