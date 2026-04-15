package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.DisposalQueryDTO;
import com.museum.museumsystem.entity.Disposal;
import com.museum.museumsystem.service.DisposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/disposal")
@RequiredArgsConstructor
public class DisposalController {

    private final DisposalService disposalService;

    @PostMapping("/page")
    public Result<PageResult<Disposal>> page(@RequestBody @Valid DisposalQueryDTO queryDTO) {
        return Result.success(disposalService.pageQuery(queryDTO));
    }

    @GetMapping("/{id}")
    public Result<Disposal> getById(@PathVariable Long id) {
        return Result.success(disposalService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody @Valid Disposal disposal) {
        disposal.setOperator(1L); // 当前用户ID
        disposalService.save(disposal);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody @Valid Disposal disposal) {
        disposalService.updateById(disposal);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        disposalService.removeById(id);
        return Result.success();
    }
}