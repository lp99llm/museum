package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.AcquisitionQueryDTO;
import com.museum.museumsystem.entity.Acquisition;
import com.museum.museumsystem.security.SecurityUtils;
import com.museum.museumsystem.service.AcquisitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/acquisition")
@RequiredArgsConstructor
public class AcquisitionController {

    private final AcquisitionService acquisitionService;

    @PostMapping("/page")
    @PreAuthorize("hasAuthority('acquisition:view')")
    public Result<PageResult<Acquisition>> page(@RequestBody @Valid AcquisitionQueryDTO queryDTO) {
        return Result.success(acquisitionService.pageQuery(queryDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('acquisition:view')")
    public Result<Acquisition> getById(@PathVariable Long id) {
        return Result.success(acquisitionService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('acquisition:add')")
    public Result<Void> add(@RequestBody @Valid Acquisition acquisition) {
        acquisition.setCreatedBy(SecurityUtils.getCurrentUserId());
        acquisitionService.save(acquisition);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('acquisition:edit')")
    public Result<Void> update(@RequestBody @Valid Acquisition acquisition) {
        acquisitionService.updateById(acquisition);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('acquisition:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        acquisitionService.removeById(id);
        return Result.success();
    }
}
