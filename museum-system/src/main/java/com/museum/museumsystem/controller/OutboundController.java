package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.OutboundQueryDTO;
import com.museum.museumsystem.entity.Outbound;
import com.museum.museumsystem.entity.OutboundFlow;
import com.museum.museumsystem.service.OutboundService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/outbound")
@RequiredArgsConstructor
public class OutboundController {

    private final OutboundService outboundService;

    @PostMapping("/page")
    public Result<PageResult<Outbound>> page(@RequestBody @Valid OutboundQueryDTO queryDTO) {
        return Result.success(outboundService.pageQuery(queryDTO));
    }

    @GetMapping("/{id}")
    public Result<Outbound> getById(@PathVariable Long id) {
        return Result.success(outboundService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody @Valid Outbound outbound) {
        outbound.setStatus("PENDING");
        outbound.setCurrentStage("STAGE1");
        outboundService.save(outbound);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody @Valid Outbound outbound) {
        outboundService.updateById(outbound);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        outboundService.removeById(id);
        return Result.success();
    }

    @PostMapping("/approve")
    public Result<Void> approve(@RequestBody Map<String, String> params) {
        Long outboundId = Long.parseLong(params.get("outboundId"));
        String stage = params.get("stage");
        String approverName = params.get("approverName");
        String approverRole = params.get("approverRole");
        String opinion = params.get("opinion");
        String status = params.get("status");
        outboundService.approve(outboundId, stage, approverName, approverRole, opinion, status);
        return Result.success();
    }

    @PostMapping("/return")
    public Result<Void> returnArtifact(@RequestBody Map<String, String> params) {
        Long outboundId = Long.parseLong(params.get("outboundId"));
        String returnImage = params.get("returnImage");
        String returnRemarks = params.get("returnRemarks");
        outboundService.returnArtifact(outboundId, returnImage, returnRemarks);
        return Result.success();
    }

    @GetMapping("/{id}/flow")
    public Result<List<OutboundFlow>> getFlowHistory(@PathVariable Long id) {
        return Result.success(outboundService.getFlowHistory(id));
    }
}
