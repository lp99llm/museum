package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface UserService extends IService<User> {
    UserDetails loadUserByUsername(String username);
    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean saveUser(User user);
    PageResult<User> pageQuery(Long current, Long size, String keyword);
    User getDetailWithLoginInfo(Long id);
    void fillLastLoginInfo(List<User> users);
    void updateLastLoginInfo(Long userId, String ip, LocalDateTime loginTime);
    void resetPassword(Long id, String newPassword);
    void exportUsers(String keyword, HttpServletResponse response) throws IOException;
    List<Map<String, Object>> getUserLogs(Long id);
}
