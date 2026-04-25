package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.StorageCheckQueryDTO;
import com.museum.museumsystem.entity.StorageCheck;
import com.museum.museumsystem.security.SecurityUtils;
import com.museum.museumsystem.service.StorageCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/storage-check")
@RequiredArgsConstructor
public class StorageCheckController {

    private final StorageCheckService storageCheckService;

    @PostMapping("/page")
    @PreAuthorize("hasAuthority('storage-check:view')")
    public Result<PageResult<StorageCheck>> page(@RequestBody @Valid StorageCheckQueryDTO queryDTO) {
        return Result.success(storageCheckService.pageQuery(queryDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('storage-check:view')")
    public Result<StorageCheck> getById(@PathVariable Long id) {
        return Result.success(storageCheckService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('storage-check:add')")
    public Result<Void> add(@RequestBody @Valid StorageCheck storageCheck) {
        storageCheck.setChecker(SecurityUtils.getCurrentUserId());
        storageCheckService.save(storageCheck);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('storage-check:edit')")
    public Result<Void> update(@RequestBody @Valid StorageCheck storageCheck) {
        storageCheckService.updateById(storageCheck);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('storage-check:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        storageCheckService.removeById(id);
        return Result.success();
    }
}
