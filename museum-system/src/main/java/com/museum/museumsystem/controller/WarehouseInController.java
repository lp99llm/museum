package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.WarehouseInQueryDTO;
import com.museum.museumsystem.entity.WarehouseIn;
import com.museum.museumsystem.service.WarehouseInService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/warehouse-in")
@RequiredArgsConstructor
public class WarehouseInController {

    private final WarehouseInService warehouseInService;

    @PostMapping("/page")
    public Result<PageResult<WarehouseIn>> page(@RequestBody @Valid WarehouseInQueryDTO queryDTO) {
        return Result.success(warehouseInService.pageQuery(queryDTO));
    }

    @GetMapping("/{id}")
    public Result<WarehouseIn> getById(@PathVariable Long id) {
        return Result.success(warehouseInService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody @Valid WarehouseIn warehouseIn) {
        warehouseIn.setOperator(1L); // 当前用户ID
        warehouseInService.save(warehouseIn);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody @Valid WarehouseIn warehouseIn) {
        warehouseInService.updateById(warehouseIn);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        warehouseInService.removeById(id);
        return Result.success();
    }
}