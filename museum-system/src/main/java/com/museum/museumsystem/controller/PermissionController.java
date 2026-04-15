package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.entity.Permission;
import com.museum.museumsystem.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('permission:view')")
    public Result<List<Permission>> tree() {
        return Result.success(permissionService.getPermissionTree());
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('permission:view')")
    public Result<List<Permission>> list() {
        return Result.success(permissionService.list());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:view')")
    public Result<Permission> getById(@PathVariable Long id) {
        return Result.success(permissionService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('permission:add')")
    public Result<Void> add(@RequestBody @Valid Permission permission) {
        permissionService.save(permission);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('permission:edit')")
    public Result<Void> update(@RequestBody @Valid Permission permission) {
        permissionService.updateById(permission);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.removeById(id);
        return Result.success();
    }

    // 获取角色拥有的权限（带标记）
    @GetMapping("/role/{roleId}")
    @PreAuthorize("hasAuthority('role:assign')")
    public Result<List<Permission>> getPermissionsWithFlag(@PathVariable Long roleId) {
        return Result.success(permissionService.getPermissionsWithFlag(roleId));
    }
}