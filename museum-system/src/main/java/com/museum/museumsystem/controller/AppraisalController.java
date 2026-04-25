package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.AppraisalQueryDTO;
import com.museum.museumsystem.entity.Appraisal;
import com.museum.museumsystem.service.AppraisalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/appraisal")
@RequiredArgsConstructor
public class AppraisalController {

    private final AppraisalService appraisalService;

    @PostMapping("/page")
    @PreAuthorize("hasAuthority('appraisal:view')")
    public Result<PageResult<Appraisal>> page(@RequestBody @Valid AppraisalQueryDTO queryDTO) {
        return Result.success(appraisalService.pageQuery(queryDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('appraisal:view')")
    public Result<Appraisal> getById(@PathVariable Long id) {
        return Result.success(appraisalService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('appraisal:add')")
    public Result<Void> add(@RequestBody @Valid Appraisal appraisal) {
        appraisalService.save(appraisal);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('appraisal:edit')")
    public Result<Void> update(@RequestBody @Valid Appraisal appraisal) {
        appraisalService.updateById(appraisal);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('appraisal:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        appraisalService.removeById(id);
        return Result.success();
    }
}
