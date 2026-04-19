package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.ExhibitionArtifactDTO;
import com.museum.museumsystem.dto.request.ExhibitionQueryDTO;
import com.museum.museumsystem.entity.Exhibition;
import com.museum.museumsystem.service.ExhibitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/exhibition")
@RequiredArgsConstructor
public class ExhibitionController {

    private final ExhibitionService exhibitionService;

    @PostMapping("/page")
    public Result<PageResult<Exhibition>> page(@RequestBody @Valid ExhibitionQueryDTO queryDTO) {
        return Result.success(exhibitionService.pageQuery(queryDTO));
    }

    @GetMapping("/{id}")
    public Result<Exhibition> getById(@PathVariable Long id) {
        return Result.success(exhibitionService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody @Valid Exhibition exhibition) {
        exhibition.setCreatedBy(1L);
        exhibitionService.save(exhibition);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody @Valid Exhibition exhibition) {
        exhibitionService.updateById(exhibition);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        exhibitionService.removeById(id);
        return Result.success();
    }

    @PostMapping("/{exhibitionId}/artifact/{artifactId}")
    public Result<Void> addArtifact(
            @PathVariable Long exhibitionId,
            @PathVariable Long artifactId,
            @RequestParam(required = false) Integer displayOrder,
            @RequestParam(required = false) String remark) {
        exhibitionService.addArtifactToExhibition(exhibitionId, artifactId, displayOrder, remark);
        return Result.success();
    }

    @DeleteMapping("/{exhibitionId}/artifact/{artifactId}")
    public Result<Void> removeArtifact(
            @PathVariable Long exhibitionId,
            @PathVariable Long artifactId) {
        exhibitionService.removeArtifactFromExhibition(exhibitionId, artifactId);
        return Result.success();
    }

    @GetMapping("/{exhibitionId}/artifacts")
    public Result<List<Long>> getArtifactIds(@PathVariable Long exhibitionId) {
        return Result.success(exhibitionService.getArtifactIdsByExhibitionId(exhibitionId));
    }

    @GetMapping("/{exhibitionId}/artifact-details")
    public Result<List<ExhibitionArtifactDTO>> getArtifactDetails(@PathVariable Long exhibitionId) {
        return Result.success(exhibitionService.getArtifactDetailsByExhibitionId(exhibitionId));
    }
}
