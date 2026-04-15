package com.museum.museumsystem.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.museum.museumsystem.entity.Permission;
import com.museum.museumsystem.entity.RolePermission;
import com.museum.museumsystem.entity.User;
import com.museum.museumsystem.entity.UserRole;
import com.museum.museumsystem.mapper.UserMapper;
import com.museum.museumsystem.service.PermissionService;
import com.museum.museumsystem.service.RolePermissionService;
import com.museum.museumsystem.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;
    private final UserRoleService userRoleService;
    private final RolePermissionService rolePermissionService;
    private final PermissionService permissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 查询用户角色
        List<Long> roleIds = userRoleService.getRoleIdsByUserId(user.getId());

        // 查询角色对应的权限ID
        List<Long> permissionIds = new ArrayList<>();
        for (Long roleId : roleIds) {
            permissionIds.addAll(rolePermissionService.getPermissionIdsByRoleId(roleId));
        }

        // 去重
        permissionIds = permissionIds.stream().distinct().collect(Collectors.toList());

        // 查询权限编码
        List<String> permissions = new ArrayList<>();
        if (!permissionIds.isEmpty()) {
            List<Permission> permissionList = permissionService.listByIds(permissionIds);
            permissions = permissionList.stream()
                    .map(Permission::getPermCode)
                    .collect(Collectors.toList());
        }

        // 构建 CustomUserDetails
        CustomUserDetails userDetails = new CustomUserDetails();
        userDetails.setUserId(user.getId());
        userDetails.setUsername(user.getUsername());
        userDetails.setPassword(user.getPassword());
        userDetails.setEnabled(user.getEnabled());
        userDetails.setPermissions(permissions);

        return userDetails;
    }
}