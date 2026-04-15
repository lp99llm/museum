package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.entity.RolePermission;
import com.museum.museumsystem.mapper.RolePermissionMapper;
import com.museum.museumsystem.service.RolePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {

    @Override
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        return this.list(wrapper).stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        // 删除角色原有权限
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        this.remove(wrapper);

        // 添加新权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<RolePermission> rolePermissions = permissionIds.stream().map(permId -> {
                RolePermission rp = new RolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(permId);
                return rp;
            }).collect(Collectors.toList());
            return this.saveBatch(rolePermissions);
        }
        return true;
    }
}