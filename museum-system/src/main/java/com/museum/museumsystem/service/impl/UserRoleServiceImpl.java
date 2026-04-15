package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.entity.UserRole;
import com.museum.museumsystem.mapper.UserRoleMapper;
import com.museum.museumsystem.service.UserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        return this.list(wrapper).stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean assignRolesToUser(Long userId, List<Long> roleIds) {
        // 删除用户原有角色
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        this.remove(wrapper);

        // 添加新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            List<UserRole> userRoles = roleIds.stream().map(roleId -> {
                UserRole ur = new UserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                return ur;
            }).collect(Collectors.toList());
            return this.saveBatch(userRoles);
        }
        return true;
    }
}