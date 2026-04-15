package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.AcquisitionQueryDTO;
import com.museum.museumsystem.entity.Acquisition;
import com.museum.museumsystem.service.AcquisitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/acquisition")
@RequiredArgsConstructor
public class AcquisitionController {

    private final AcquisitionService acquisitionService;

    @PostMapping("/page")
    public Result<PageResult<Acquisition>> page(@RequestBody @Valid AcquisitionQueryDTO queryDTO) {
        return Result.success(acquisitionService.pageQuery(queryDTO));
    }

    @GetMapping("/{id}")
    public Result<Acquisition> getById(@PathVariable Long id) {
        return Result.success(acquisitionService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody @Valid Acquisition acquisition) {
        acquisition.setCreatedBy(1L); // 当前用户ID
        acquisitionService.save(acquisition);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody @Valid Acquisition acquisition) {
        acquisitionService.updateById(acquisition);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        acquisitionService.removeById(id);
        return Result.success();
    }
}