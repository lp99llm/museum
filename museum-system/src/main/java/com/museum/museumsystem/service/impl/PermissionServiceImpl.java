package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.entity.Permission;
import com.museum.museumsystem.mapper.PermissionMapper;
import com.museum.museumsystem.service.PermissionService;
import com.museum.museumsystem.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    private final RolePermissionService rolePermissionService;

    @Override
    public List<Permission> getPermissionTree() {
        // 查询所有权限
        List<Permission> allPermissions = this.list(new LambdaQueryWrapper<Permission>()
                .orderByAsc(Permission::getSort));
        // 构建树形结构
        return buildTree(allPermissions, 0L);
    }

    private List<Permission> buildTree(List<Permission> allPermissions, Long parentId) {
        List<Permission> tree = new ArrayList<>();
        for (Permission perm : allPermissions) {
            if (perm.getParentId() != null && perm.getParentId().equals(parentId)) {
                List<Permission> children = buildTree(allPermissions, perm.getId());
                perm.setChildren(children);
                tree.add(perm);
            }
        }
        return tree;
    }

    @Override
    public List<Permission> getPermissionsWithFlag(Long roleId) {
        // 查询所有权限
        List<Permission> allPermissions = this.list();
        // 查询角色拥有的权限ID列表
        List<Long> ownedPermIds = rolePermissionService.getPermissionIdsByRoleId(roleId);
        // 标记是否拥有
        allPermissions.forEach(perm -> perm.setOwned(ownedPermIds.contains(perm.getId())));
        return allPermissions;
    }
}