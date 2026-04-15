package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.entity.Role;
import com.museum.museumsystem.mapper.RoleMapper;
import com.museum.museumsystem.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Override
    public PageResult<Role> pageQuery(Long current, Long size, String roleName) {
        // 创建分页对象
        Page<Role> page = new Page<>(current, size);

        // 构建查询条件
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(roleName), Role::getRoleName, roleName)
                .orderByDesc(Role::getId); // 按ID倒序或其他字段

        // 执行分页查询
        Page<Role> pageResult = this.page(page, wrapper);

        // 封装返回结果
        PageResult<Role> result = new PageResult<>();
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setPages(pageResult.getPages());
        result.setRecords(pageResult.getRecords());

        return result;
    }
}