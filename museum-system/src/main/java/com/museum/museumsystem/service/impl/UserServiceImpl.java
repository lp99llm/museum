package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.entity.User;
import com.museum.museumsystem.mapper.UserMapper;
import com.museum.museumsystem.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return this.getOne(wrapper);
    }

    @Override
    public PageResult<User> pageQuery(Long current, Long size, String username) {
        // 创建分页对象
        Page<User> page = new Page<>(current, size);

        // 构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(username), User::getUsername, username)
                .orderByDesc(User::getCreatedTime); // 按创建时间倒序

        // 执行分页查询
        Page<User> pageResult = this.page(page, wrapper);

        // 封装返回结果
        PageResult<User> result = new PageResult<>();
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setPages(pageResult.getPages());
        result.setRecords(pageResult.getRecords());

        return result;
    }
}