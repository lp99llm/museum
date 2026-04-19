package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.RestorationQueryDTO;
import com.museum.museumsystem.entity.Restoration;
import com.museum.museumsystem.entity.RestorationFlow;
import com.museum.museumsystem.service.RestorationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/restoration")
@RequiredArgsConstructor
public class RestorationController {

    private final RestorationService restorationService;

    @PostMapping("/page")
    public Result<PageResult<Restoration>> page(@RequestBody @Valid RestorationQueryDTO queryDTO) {
        return Result.success(restorationService.pageQuery(queryDTO));
    }

    @GetMapping("/{id}")
    public Result<Restoration> getById(@PathVariable Long id) {
        return Result.success(restorationService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody @Valid Restoration restoration) {
        restoration.setStatus("PENDING");
        restoration.setCurrentStage("APPLY");
        restorationService.save(restoration);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody @Valid Restoration restoration) {
        restorationService.updateById(restoration);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        restorationService.removeById(id);
        return Result.success();
    }

    @PostMapping("/approve")
    public Result<Void> approve(@RequestBody Map<String, String> params) {
        Long restorationId = Long.parseLong(params.get("restorationId"));
        String stage = params.get("stage");
        String approverName = params.get("approverName");
        String approverRole = params.get("approverRole");
        String opinion = params.get("opinion");
        String status = params.get("status");
        restorationService.approve(restorationId, stage, approverName, approverRole, opinion, status);
        return Result.success();
    }

    @GetMapping("/{id}/flow")
    public Result<List<RestorationFlow>> getFlowHistory(@PathVariable Long id) {
        return Result.success(restorationService.getFlowHistory(id));
    }
}
