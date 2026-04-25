package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.entity.OperationLog;
import com.museum.museumsystem.entity.Role;
import com.museum.museumsystem.entity.User;
import com.museum.museumsystem.mapper.OperationLogMapper;
import com.museum.museumsystem.mapper.RoleMapper;
import com.museum.museumsystem.mapper.UserMapper;
import com.museum.museumsystem.security.CustomUserDetails;
import com.museum.museumsystem.service.UserRoleService;
import com.museum.museumsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final OperationLogMapper operationLogMapper;
    private final UserRoleService userRoleService;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = getOne(wrapper);

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        CustomUserDetails userDetails = new CustomUserDetails();
        userDetails.setUserId(user.getId());
        userDetails.setUsername(user.getUsername());
        userDetails.setPassword(user.getPassword());
        userDetails.setEnabled(user.isEnabled());
        userDetails.setPermissions(Collections.emptyList());
        return userDetails;
    }

    @Override
    public User findByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return getOne(wrapper);
    }

    @Override
    public boolean existsByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return count(wrapper) > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        return count(wrapper) > 0;
    }

    @Override
    public boolean saveUser(User user) {
        return save(user);
    }

    @Override
    public PageResult<User> pageQuery(Long current, Long size, String keyword) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(query -> query.like(User::getUsername, keyword).or().like(User::getRealName, keyword));
        }

        page(page, wrapper);
        fillRoleInfo(page.getRecords());
        fillLastLoginInfo(page.getRecords());

        PageResult<User> result = new PageResult<>();
        result.setCurrent(page.getCurrent());
        result.setSize(page.getSize());
        result.setTotal(page.getTotal());
        result.setPages(page.getPages());
        result.setRecords(page.getRecords());
        return result;
    }

    @Override
    public User getDetailWithLoginInfo(Long id) {
        User user = getById(id);
        if (user == null) {
            return null;
        }
        List<User> users = new ArrayList<>();
        users.add(user);
        fillRoleInfo(users);
        fillLastLoginInfo(users);
        return user;
    }

    @Override
    public void fillLastLoginInfo(List<User> users) {
        if (users == null || users.isEmpty()) {
            return;
        }
        Set<Long> userIds = users.stream().map(User::getId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (userIds.isEmpty()) {
            return;
        }

        List<OperationLog> logs = operationLogMapper.selectList(new LambdaQueryWrapper<OperationLog>()
                .in(OperationLog::getOperator, userIds)
                .eq(OperationLog::getOperationType, "LOGIN")
                .eq(OperationLog::getResult, "SUCCESS")
                .orderByDesc(OperationLog::getOperationTime));

        Map<Long, OperationLog> latestMap = new HashMap<>();
        for (OperationLog log : logs) {
            if (!latestMap.containsKey(log.getOperator())) {
                latestMap.put(log.getOperator(), log);
            }
        }

        for (User user : users) {
            OperationLog log = latestMap.get(user.getId());
            if (log != null) {
                user.setLastLoginTime(log.getOperationTime());
                user.setLastLoginIp(log.getRequestIp());
            }
        }
    }

    @Override
    public void updateLastLoginInfo(Long userId, String ip, LocalDateTime loginTime) {
        OperationLog log = new OperationLog();
        log.setOperator(userId);
        log.setOperationModule("auth");
        log.setOperationType("LOGIN");
        log.setOperationDesc("用户登录");
        log.setRequestIp(ip);
        log.setResult("SUCCESS");
        log.setOperationTime(loginTime == null ? LocalDateTime.now() : loginTime);
        operationLogMapper.insert(log);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        User user = getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
    }

    @Override
    public void exportUsers(String keyword, HttpServletResponse response) throws IOException {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(query -> query.like(User::getUsername, keyword).or().like(User::getRealName, keyword));
        }
        List<User> users = list(wrapper);
        fillRoleInfo(users);
        fillLastLoginInfo(users);

        StringBuilder csv = new StringBuilder();
        csv.append('\ufeff');
        csv.append("ID,用户名,真实姓名,邮箱,手机号,角色,状态,最后登录时间,最后登录IP,创建时间\n");
        for (User user : users) {
            csv.append(csvValue(user.getId())).append(',')
                    .append(csvValue(user.getUsername())).append(',')
                    .append(csvValue(user.getRealName())).append(',')
                    .append(csvValue(user.getEmail())).append(',')
                    .append(csvValue(user.getPhone())).append(',')
                    .append(csvValue(user.getRole())).append(',')
                    .append(csvValue(user.isEnabled() ? "ENABLED" : "DISABLED")).append(',')
                    .append(csvValue(user.getLastLoginTime())).append(',')
                    .append(csvValue(user.getLastLoginIp())).append(',')
                    .append(csvValue(user.getCreatedTime()))
                    .append('\n');
        }

        String fileName = URLEncoder.encode("users-export.csv", StandardCharsets.UTF_8.name());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);
        response.getWriter().write(csv.toString());
        response.getWriter().flush();
    }

    @Override
    public List<Map<String, Object>> getUserLogs(Long id) {
        User user = getById(id);
        if (user == null) {
            return Collections.emptyList();
        }

        List<OperationLog> allLogs = new ArrayList<>();
        allLogs.addAll(operationLogMapper.selectList(new LambdaQueryWrapper<OperationLog>()
                .eq(OperationLog::getOperator, id)
                .orderByDesc(OperationLog::getOperationTime)));

        List<OperationLog> userModuleLogs = operationLogMapper.selectList(new LambdaQueryWrapper<OperationLog>()
                .eq(OperationLog::getOperationModule, "user")
                .orderByDesc(OperationLog::getOperationTime));
        for (OperationLog log : userModuleLogs) {
            String desc = log.getOperationDesc();
            if (desc != null && (desc.contains("用户#" + id) || desc.contains(user.getUsername()))) {
                allLogs.add(log);
            }
        }

        Map<Long, User> operatorMap = buildOperatorMap(allLogs);
        return allLogs.stream()
                .sorted(Comparator.comparing(OperationLog::getOperationTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(log -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    User operator = operatorMap.get(log.getOperator());
                    item.put("time", log.getOperationTime());
                    item.put("operator", operator != null ? operator.getUsername() : String.valueOf(log.getOperator()));
                    item.put("type", log.getOperationType());
                    item.put("content", log.getOperationDesc());
                    item.put("result", log.getResult());
                    return item;
                })
                .collect(Collectors.toList());
    }

    private void fillRoleInfo(List<User> users) {
        if (users == null || users.isEmpty()) {
            return;
        }
        for (User user : users) {
            List<Long> roleIds = userRoleService.getRoleIdsByUserId(user.getId());
            user.setRoleIds(roleIds);
            if (roleIds == null || roleIds.isEmpty()) {
                user.setRole("未分配角色");
                continue;
            }
            List<Role> roles = roleMapper.selectBatchIds(roleIds);
            user.setRole(roles.stream().map(Role::getRoleName).collect(Collectors.joining("、")));
        }
    }

    private Map<Long, User> buildOperatorMap(List<OperationLog> logs) {
        Set<Long> ids = logs.stream().map(OperationLog::getOperator).filter(Objects::nonNull).collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return listByIds(ids).stream().collect(Collectors.toMap(User::getId, item -> item));
    }

    private String csvValue(Object value) {
        String text = value == null ? "" : String.valueOf(value);
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }
}
