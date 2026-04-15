package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.entity.Permission;

import java.util.List;
import java.util.Map;

public interface PermissionService extends IService<Permission> {
    // 获取权限树（按父子关系组织）
    List<Permission> getPermissionTree();

    // 获取权限列表，并标记角色拥有的权限
    List<Permission> getPermissionsWithFlag(Long roleId);
}