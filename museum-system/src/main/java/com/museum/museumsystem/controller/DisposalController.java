package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.DisposalQueryDTO;
import com.museum.museumsystem.entity.Disposal;
import com.museum.museumsystem.entity.DisposalFlow;
import com.museum.museumsystem.security.SecurityUtils;
import com.museum.museumsystem.service.DisposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/disposal")
@RequiredArgsConstructor
public class DisposalController {

    private final DisposalService disposalService;

    @PostMapping("/page")
    @PreAuthorize("hasAuthority('disposal:view')")
    public Result<PageResult<Disposal>> page(@RequestBody @Valid DisposalQueryDTO queryDTO) {
        return Result.success(disposalService.pageQuery(queryDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('disposal:view')")
    public Result<Disposal> getById(@PathVariable Long id) {
        return Result.success(disposalService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('disposal:add')")
    public Result<Void> add(@RequestBody @Valid Disposal disposal) {
        disposal.setCreatedBy(SecurityUtils.getCurrentUserId());
        disposal.setStatus("PENDING");
        disposal.setCurrentStage("APPLY");
        disposalService.save(disposal);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('disposal:edit')")
    public Result<Void> update(@RequestBody @Valid Disposal disposal) {
        disposalService.updateById(disposal);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('disposal:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        disposalService.removeById(id);
        return Result.success();
    }

    @PostMapping("/approve")
    @PreAuthorize("hasAuthority('disposal:approve')")
    public Result<Void> approve(@RequestBody Map<String, String> params) {
        Long disposalId = Long.parseLong(params.get("disposalId"));
        String stage = params.get("stage");
        String approverName = params.get("approverName");
        String approverRole = params.get("approverRole");
        String opinion = params.get("opinion");
        String status = params.get("status");
        disposalService.approve(disposalId, stage, approverName, approverRole, opinion, status);
        return Result.success();
    }

    @GetMapping("/{id}/flow")
    @PreAuthorize("hasAuthority('disposal:view')")
    public Result<List<DisposalFlow>> getFlowHistory(@PathVariable Long id) {
        return Result.success(disposalService.getFlowHistory(id));
    }
}
