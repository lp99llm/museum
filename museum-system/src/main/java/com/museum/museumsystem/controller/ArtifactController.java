package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.AuditLogService;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.dto.request.ArtifactExportRequest;
import com.museum.museumsystem.dto.request.ArtifactImportRequest;
import com.museum.museumsystem.dto.request.ArtifactJoinExhibitionRequest;
import com.museum.museumsystem.dto.request.ArtifactQueryDTO;
import com.museum.museumsystem.entity.Artifact;
import com.museum.museumsystem.service.ArtifactService;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/artifact")
@RequiredArgsConstructor
public class ArtifactController {

    private final ArtifactService artifactService;
    private final AuditLogService auditLogService;

    @PostMapping("/page")
    @PreAuthorize("hasAuthority('artifact:view')")
    public Result<PageResult<Artifact>> pageQuery(@RequestBody ArtifactQueryDTO queryDTO) {
        return Result.success(artifactService.pageQuery(queryDTO));
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAuthority('artifact:view')")
    public Result<Artifact> getByCode(@PathVariable String code) {
        return Result.success(artifactService.getByCode(code));
    }

    @PutMapping("/state/{id}")
    @PreAuthorize("hasAuthority('artifact:edit')")
    public Result<Void> updateState(@PathVariable Long id,
                                    @RequestParam String targetState,
                                    @RequestParam String remark) {
        artifactService.updateState(id, targetState, remark);
        return Result.success();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('artifact:add')")
    public Result<Void> save(@RequestBody Artifact artifact) {
        artifactService.save(artifact);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('artifact:edit')")
    public Result<Void> update(@RequestBody Artifact artifact) {
        artifactService.updateById(artifact);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('artifact:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        artifactService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('artifact:view')")
    public Result<Artifact> getById(@PathVariable Long id) {
        return Result.success(artifactService.getById(id));
    }

    @PostMapping("/import")
    @PreAuthorize("hasAuthority('artifact:add')")
    public Result<Map<String, Object>> importArtifacts(@RequestBody @Valid ArtifactImportRequest request) {
        Map<String, Object> result = artifactService.importArtifacts(request);
        auditLogService.logSuccess("artifact", "ADD", "批量导入文物");
        return Result.success(result);
    }

    @PostMapping("/export")
    @PreAuthorize("hasAuthority('artifact:export')")
    public void export(@RequestBody(required = false) ArtifactExportRequest request, HttpServletResponse response) throws IOException {
        artifactService.exportArtifacts(request == null ? new ArtifactExportRequest() : request, response);
        auditLogService.logSuccess("artifact", "EXPORT", "导出文物列表");
    }

    @PostMapping("/export/batch")
    @PreAuthorize("hasAuthority('artifact:export')")
    public void exportBatch(@RequestBody @Valid ArtifactExportRequest request, HttpServletResponse response) throws IOException {
        artifactService.exportArtifacts(request, response);
        auditLogService.logSuccess("artifact", "EXPORT", "批量导出文物");
    }

    @GetMapping("/{id}/label")
    @PreAuthorize("hasAuthority('artifact:view')")
    public void label(@PathVariable Long id, HttpServletResponse response) throws IOException {
        artifactService.exportArtifactLabel(id, response);
        auditLogService.logSuccess("artifact", "EXPORT", "导出文物标签 " + id);
    }

    @GetMapping("/{id}/report")
    @PreAuthorize("hasAuthority('report:export')")
    public void report(@PathVariable Long id, HttpServletResponse response) throws IOException {
        artifactService.exportArtifactReport(id, response);
        auditLogService.logSuccess("artifact", "EXPORT", "导出文物档案报告 " + id);
    }

    @PostMapping("/{id}/join-exhibition")
    @PreAuthorize("hasAuthority('artifact:edit')")
    public Result<Void> joinExhibition(@PathVariable Long id, @RequestBody @Valid ArtifactJoinExhibitionRequest request) {
        artifactService.joinExhibition(id, request);
        auditLogService.logSuccess("artifact", "UPDATE", "文物#" + id + "加入展览#" + request.getExhibitionId());
        return Result.success();
    }

    @GetMapping("/{id}/relations")
    @PreAuthorize("hasAuthority('artifact:view')")
    public Result<Map<String, Object>> relations(@PathVariable Long id) {
        return Result.success(artifactService.getRelations(id));
    }
}
