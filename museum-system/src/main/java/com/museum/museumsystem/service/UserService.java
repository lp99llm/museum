package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService extends IService<User> {
    UserDetails loadUserByUsername(String username);
    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean saveUser(User user);
    PageResult<User> pageQuery(Long current, Long size, String keyword);
}