package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.ApprovalActionDTO;
import com.museum.museumsystem.dto.request.ApprovalQueryDTO;
import com.museum.museumsystem.entity.Approval;
import com.museum.museumsystem.security.SecurityUtils;
import com.museum.museumsystem.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/approval")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    @PostMapping("/page")
    @PreAuthorize("hasAuthority('approval:view')")
    public Result<PageResult<Approval>> page(@RequestBody @Valid ApprovalQueryDTO queryDTO) {
        return Result.success(approvalService.pageQuery(queryDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('approval:view')")
    public Result<Approval> getById(@PathVariable Long id) {
        return Result.success(approvalService.getById(id));
    }

    // 提交审批（通常由其他业务模块调用，这里作为备用接口）
    @PostMapping("/submit")
    @PreAuthorize("hasAuthority('approval:submit')")
    public Result<Approval> submit(@RequestParam String bizType,
                                   @RequestParam Long bizId,
                                   @RequestParam String currentNode) {
        Approval approval = approvalService.submitApproval(bizType, bizId, SecurityUtils.getCurrentUserId(), currentNode);
        return Result.success(approval);
    }

    // 审批操作（通过/驳回）
    @PostMapping("/process")
    @PreAuthorize("hasAuthority('approval:process')")
    public Result<Void> process(@RequestBody @Valid ApprovalActionDTO actionDTO) {
        approvalService.processApproval(
                actionDTO.getApprovalId(),
                actionDTO.getApprovalStatus(),
                SecurityUtils.getCurrentUserId(),
                actionDTO.getApproveOpinion()
        );
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('approval:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        approvalService.removeById(id);
        return Result.success();
    }
}
