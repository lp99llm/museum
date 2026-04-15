package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.StorageCheckQueryDTO;
import com.museum.museumsystem.entity.StorageCheck;
import com.museum.museumsystem.service.StorageCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/storage-check")
@RequiredArgsConstructor
public class StorageCheckController {

    private final StorageCheckService storageCheckService;

    @PostMapping("/page")
    public Result<PageResult<StorageCheck>> page(@RequestBody @Valid StorageCheckQueryDTO queryDTO) {
        return Result.success(storageCheckService.pageQuery(queryDTO));
    }

    @GetMapping("/{id}")
    public Result<StorageCheck> getById(@PathVariable Long id) {
        return Result.success(storageCheckService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody @Valid StorageCheck storageCheck) {
        storageCheck.setChecker(1L); // 当前用户ID
        storageCheckService.save(storageCheck);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody @Valid StorageCheck storageCheck) {
        storageCheckService.updateById(storageCheck);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        storageCheckService.removeById(id);
        return Result.success();
    }
}