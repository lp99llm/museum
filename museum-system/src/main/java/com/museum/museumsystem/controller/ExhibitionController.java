package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.AuditLogService;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.dto.request.ExhibitionArtifactDTO;
import com.museum.museumsystem.dto.request.ExhibitionMaterialUploadRequest;
import com.museum.museumsystem.dto.request.ExhibitionQueryDTO;
import com.museum.museumsystem.entity.Exhibition;
import com.museum.museumsystem.security.SecurityUtils;
import com.museum.museumsystem.service.ExhibitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exhibition")
@RequiredArgsConstructor
public class ExhibitionController {

    private final ExhibitionService exhibitionService;
    private final AuditLogService auditLogService;

    @PostMapping("/page")
    @PreAuthorize("hasAuthority('exhibition:view')")
    public Result<PageResult<Exhibition>> page(@RequestBody @Valid ExhibitionQueryDTO queryDTO) {
        return Result.success(exhibitionService.pageQuery(queryDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('exhibition:view')")
    public Result<Exhibition> getById(@PathVariable Long id) {
        return Result.success(exhibitionService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('exhibition:add')")
    public Result<Map<String, Object>> add(@RequestBody @Valid Exhibition exhibition) {
        exhibition.setCreatedBy(SecurityUtils.getCurrentUserId());
        exhibitionService.save(exhibition);
        auditLogService.logSuccess("exhibition", "ADD", "create exhibition " + exhibition.getName());
        return Result.success(Map.of("id", exhibition.getId()));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('exhibition:edit')")
    public Result<Void> update(@RequestBody @Valid Exhibition exhibition) {
        exhibitionService.updateExhibition(exhibition);
        auditLogService.logSuccess("exhibition", "UPDATE", "update exhibition #" + exhibition.getId());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('exhibition:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        exhibitionService.removeById(id);
        auditLogService.logSuccess("exhibition", "DELETE", "delete exhibition #" + id);
        return Result.success();
    }

    @PostMapping("/{exhibitionId}/artifact/{artifactId}")
    @PreAuthorize("hasAuthority('exhibition:edit')")
    public Result<Void> addArtifact(
            @PathVariable Long exhibitionId,
            @PathVariable Long artifactId,
            @RequestParam(required = false) Integer displayOrder,
            @RequestParam(required = false) String remark) {
        exhibitionService.addArtifactToExhibition(exhibitionId, artifactId, displayOrder, remark);
        return Result.success();
    }

    @DeleteMapping("/{exhibitionId}/artifact/{artifactId}")
    @PreAuthorize("hasAuthority('exhibition:edit')")
    public Result<Void> removeArtifact(
            @PathVariable Long exhibitionId,
            @PathVariable Long artifactId) {
        exhibitionService.removeArtifactFromExhibition(exhibitionId, artifactId);
        return Result.success();
    }

    @GetMapping("/{exhibitionId}/artifacts")
    @PreAuthorize("hasAuthority('exhibition:view')")
    public Result<List<Long>> getArtifactIds(@PathVariable Long exhibitionId) {
        return Result.success(exhibitionService.getArtifactIdsByExhibitionId(exhibitionId));
    }

    @GetMapping("/{exhibitionId}/artifact-details")
    @PreAuthorize("hasAuthority('exhibition:view')")
    public Result<List<ExhibitionArtifactDTO>> getArtifactDetails(@PathVariable Long exhibitionId) {
        return Result.success(exhibitionService.getArtifactDetailsByExhibitionId(exhibitionId));
    }

    @GetMapping("/{id}/artifacts/export")
    @PreAuthorize("hasAuthority('report:export')")
    public void exportArtifacts(@PathVariable Long id, HttpServletResponse response) throws IOException {
        exhibitionService.exportArtifacts(id, response);
        auditLogService.logSuccess("exhibition", "EXPORT", "export exhibition artifacts #" + id);
    }

    @GetMapping("/{id}/evaluation-report")
    @PreAuthorize("hasAuthority('report:export')")
    public void evaluationReport(@PathVariable Long id, HttpServletResponse response) throws IOException {
        exhibitionService.exportEvaluationReport(id, response);
        auditLogService.logSuccess("exhibition", "EXPORT", "export exhibition evaluation #" + id);
    }

    @PostMapping("/{id}/materials")
    @PreAuthorize("hasAuthority('exhibition:edit')")
    public Result<List<Map<String, Object>>> uploadMaterial(
            @PathVariable Long id,
            @RequestBody @Valid ExhibitionMaterialUploadRequest request) {
        List<Map<String, Object>> result = exhibitionService.uploadMaterial(id, request);
        auditLogService.logSuccess("exhibition", "UPDATE", "archive exhibition material link #" + id);
        return Result.success(result);
    }

    @PostMapping("/{id}/materials/file")
    @PreAuthorize("hasAuthority('exhibition:edit')")
    public Result<List<Map<String, Object>>> uploadMaterialFile(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String type,
            @RequestParam MultipartFile file) throws IOException {
        List<Map<String, Object>> result = exhibitionService.uploadMaterialFile(id, name, type, file);
        auditLogService.logSuccess("exhibition", "UPDATE", "upload exhibition material file #" + id);
        return Result.success(result);
    }

    @GetMapping("/{id}/materials")
    @PreAuthorize("hasAuthority('exhibition:view')")
    public Result<List<Map<String, Object>>> materials(@PathVariable Long id) {
        return Result.success(exhibitionService.getMaterials(id));
    }

    @DeleteMapping("/{id}/materials/{materialId}")
    @PreAuthorize("hasAuthority('exhibition:edit')")
    public Result<Void> deleteMaterial(@PathVariable Long id, @PathVariable Long materialId) {
        exhibitionService.deleteMaterial(id, materialId);
        auditLogService.logSuccess("exhibition", "DELETE", "delete exhibition material #" + id + "-" + materialId);
        return Result.success();
    }

    @GetMapping("/{id}/materials/{materialId}/download")
    @PreAuthorize("hasAuthority('exhibition:view')")
    public void downloadMaterial(@PathVariable Long id, @PathVariable Long materialId, HttpServletResponse response)
            throws IOException {
        exhibitionService.downloadMaterial(id, materialId, response);
    }

    @GetMapping("/{id}/visitor-stats")
    @PreAuthorize("hasAuthority('exhibition:view')")
    public Result<Map<String, Object>> visitorStats(@PathVariable Long id) {
        return Result.success(exhibitionService.getVisitorStats(id));
    }
}
