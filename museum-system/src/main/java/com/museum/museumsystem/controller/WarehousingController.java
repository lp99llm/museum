package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.WarehousingQueryDTO;
import com.museum.museumsystem.entity.Warehousing;
import com.museum.museumsystem.service.WarehousingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/warehousing")
@RequiredArgsConstructor
public class WarehousingController {

    private final WarehousingService warehousingService;

    @PostMapping("/page")
    @PreAuthorize("hasAuthority('warehousing:view')")
    public Result<PageResult<Warehousing>> page(@RequestBody @Valid WarehousingQueryDTO queryDTO) {
        return Result.success(warehousingService.pageQuery(queryDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('warehousing:view')")
    public Result<Warehousing> getById(@PathVariable Long id) {
        return Result.success(warehousingService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('warehousing:add')")
    public Result<Void> add(@RequestBody @Valid Warehousing warehousing) {
        warehousingService.save(warehousing);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('warehousing:edit')")
    public Result<Void> update(@RequestBody @Valid Warehousing warehousing) {
        warehousingService.updateById(warehousing);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('warehousing:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        warehousingService.removeById(id);
        return Result.success();
    }
}
