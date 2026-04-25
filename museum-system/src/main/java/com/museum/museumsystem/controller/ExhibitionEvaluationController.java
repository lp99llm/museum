package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.ExhibitionEvaluationDTO;
import com.museum.museumsystem.dto.request.ExhibitionEvaluationQueryDTO;
import com.museum.museumsystem.entity.ExhibitionEvaluation;
import com.museum.museumsystem.security.SecurityUtils;
import com.museum.museumsystem.service.ExhibitionEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exhibition-evaluation")
@RequiredArgsConstructor
public class ExhibitionEvaluationController {

    private final ExhibitionEvaluationService exhibitionEvaluationService;

    @PostMapping("/page")
    @PreAuthorize("hasAuthority('exhibition-evaluation:view')")
    public Result<PageResult<ExhibitionEvaluationDTO>> page(@RequestBody ExhibitionEvaluationQueryDTO queryDTO) {
        return Result.success(exhibitionEvaluationService.pageQuery(queryDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('exhibition-evaluation:view')")
    public Result<ExhibitionEvaluation> getById(@PathVariable Long id) {
        return Result.success(exhibitionEvaluationService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('exhibition-evaluation:add')")
    public Result<Void> add(@RequestBody ExhibitionEvaluation evaluation) {
        evaluation.setCreatedBy(SecurityUtils.getCurrentUserId());
        exhibitionEvaluationService.save(evaluation);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('exhibition-evaluation:edit')")
    public Result<Void> update(@RequestBody ExhibitionEvaluation evaluation) {
        exhibitionEvaluationService.updateById(evaluation);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('exhibition-evaluation:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        exhibitionEvaluationService.removeById(id);
        return Result.success();
    }
}
