package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.ExhibitionEvaluationDTO;
import com.museum.museumsystem.dto.request.ExhibitionEvaluationQueryDTO;
import com.museum.museumsystem.entity.ExhibitionEvaluation;
import com.museum.museumsystem.service.ExhibitionEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exhibition-evaluation")
@RequiredArgsConstructor
public class ExhibitionEvaluationController {

    private final ExhibitionEvaluationService exhibitionEvaluationService;

    @PostMapping("/page")
    public Result<PageResult<ExhibitionEvaluationDTO>> page(@RequestBody ExhibitionEvaluationQueryDTO queryDTO) {
        return Result.success(exhibitionEvaluationService.pageQuery(queryDTO));
    }

    @GetMapping("/{id}")
    public Result<ExhibitionEvaluation> getById(@PathVariable Long id) {
        return Result.success(exhibitionEvaluationService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody ExhibitionEvaluation evaluation) {
        evaluation.setCreatedBy(1L);
        exhibitionEvaluationService.save(evaluation);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody ExhibitionEvaluation evaluation) {
        exhibitionEvaluationService.updateById(evaluation);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        exhibitionEvaluationService.removeById(id);
        return Result.success();
    }
}
