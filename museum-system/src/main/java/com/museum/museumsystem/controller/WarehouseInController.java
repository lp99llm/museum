package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.WarehouseInQueryDTO;
import com.museum.museumsystem.entity.WarehouseIn;
import com.museum.museumsystem.security.SecurityUtils;
import com.museum.museumsystem.service.WarehouseInService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/warehouse-in")
@RequiredArgsConstructor
public class WarehouseInController {

    private final WarehouseInService warehouseInService;

    @PostMapping("/page")
    @PreAuthorize("hasAuthority('warehouse-in:view')")
    public Result<PageResult<WarehouseIn>> page(@RequestBody @Valid WarehouseInQueryDTO queryDTO) {
        return Result.success(warehouseInService.pageQuery(queryDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse-in:view')")
    public Result<WarehouseIn> getById(@PathVariable Long id) {
        return Result.success(warehouseInService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('warehouse-in:add')")
    public Result<Void> add(@RequestBody @Valid WarehouseIn warehouseIn) {
        warehouseIn.setOperator(SecurityUtils.getCurrentUserId());
        warehouseInService.save(warehouseIn);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('warehouse-in:edit')")
    public Result<Void> update(@RequestBody @Valid WarehouseIn warehouseIn) {
        warehouseInService.updateById(warehouseIn);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse-in:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        warehouseInService.removeById(id);
        return Result.success();
    }
}
