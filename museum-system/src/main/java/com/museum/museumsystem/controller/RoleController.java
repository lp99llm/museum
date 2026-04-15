package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.entity.Role;
import com.museum.museumsystem.service.RolePermissionService;
import com.museum.museumsystem.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final RolePermissionService rolePermissionService;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('role:view')")
    public Result<PageResult<Role>> page(@RequestParam(defaultValue = "1") Long current,
                                         @RequestParam(defaultValue = "10") Long size,
                                         @RequestParam(required = false) String roleName) {
        // 简易分页，可自行扩展
        PageResult<Role> pageResult = roleService.pageQuery(current, size, roleName);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role:view')")
    public Result<Role> getById(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('role:add')")
    public Result<Void> add(@RequestBody @Valid Role role) {
        roleService.save(role);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('role:edit')")
    public Result<Void> update(@RequestBody @Valid Role role) {
        roleService.updateById(role);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.removeById(id);
        return Result.success();
    }

    // 获取角色的权限ID列表
    @GetMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('role:assign')")
    public Result<List<Long>> getRolePermissions(@PathVariable Long roleId) {
        List<Long> permIds = rolePermissionService.getPermissionIdsByRoleId(roleId);
        return Result.success(permIds);
    }

    // 给角色分配权限
    @PostMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('role:assign')")
    public Result<Void> assignPermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        rolePermissionService.assignPermissionsToRole(roleId, permissionIds);
        return Result.success();
    }
}