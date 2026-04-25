package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.dto.request.UserResetPasswordDTO;
import com.museum.museumsystem.entity.User;
import com.museum.museumsystem.service.UserRoleService;
import com.museum.museumsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('user:view')")
    public Result<PageResult<User>> page(@RequestParam(defaultValue = "1") Long current,
                                         @RequestParam(defaultValue = "10") Long size,
                                         @RequestParam(required = false) String username) {
        // 简单分页，可按需扩展查询条件
        PageResult<User> pageResult = userService.pageQuery(current, size, username);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:view')")
    public Result<User> getById(@PathVariable Long id) {
        return Result.success(userService.getDetailWithLoginInfo(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('user:add')")
    public Result<Void> add(@RequestBody @Valid User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('user:edit')")
    public Result<Void> update(@RequestBody @Valid User user) {
        userService.updateById(user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success();
    }

    // 获取用户的角色 ID 列表
    @GetMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('user:assign')")
    public Result<List<Long>> getUserRoles(@PathVariable Long userId) {
        List<Long> roleIds = userRoleService.getRoleIdsByUserId(userId);
        return Result.success(roleIds);
    }

    // 给用户分配角色
    @PostMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('user:assign')")
    public Result<Void> assignRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        userRoleService.assignRolesToUser(userId, roleIds);
        return Result.success();
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasAuthority('user:reset-password')")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody @Valid UserResetPasswordDTO request) {
        userService.resetPassword(id, request.getNewPassword());
        return Result.success();
    }

    @GetMapping("/export")
    @PreAuthorize("hasAuthority('report:export')")
    public void export(@RequestParam(required = false) String username,
                       HttpServletResponse response) throws Exception {
        userService.exportUsers(username, response);
    }

    @GetMapping("/{id}/logs")
    @PreAuthorize("hasAuthority('user:view')")
    public Result<List<Map<String, Object>>> logs(@PathVariable Long id) {
        return Result.success(userService.getUserLogs(id));
    }
}
