package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.LoanQueryDTO;
import com.museum.museumsystem.dto.request.LoanReturnDTO;
import com.museum.museumsystem.entity.Loan;
import com.museum.museumsystem.entity.LoanFlow;
import com.museum.museumsystem.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/loan")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/page")
    public Result<PageResult<Loan>> page(@RequestBody @Valid LoanQueryDTO queryDTO) {
        return Result.success(loanService.pageQuery(queryDTO));
    }

    @GetMapping("/{id}")
    public Result<Loan> getById(@PathVariable Long id) {
        return Result.success(loanService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody @Valid Loan loan) {
        loan.setCreatedBy(1L);
        loan.setStatus("PENDING");
        loan.setCurrentStage("APPLY");
        loanService.save(loan);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody @Valid Loan loan) {
        loanService.updateById(loan);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        loanService.removeById(id);
        return Result.success();
    }

    @PostMapping("/return")
    public Result<Void> returnLoan(@RequestBody @Valid LoanReturnDTO returnDTO) {
        loanService.returnLoan(returnDTO.getLoanId(), returnDTO.getActualReturnDate());
        return Result.success();
    }

    @GetMapping("/artifact/{artifactId}/current")
    public Result<Loan> getCurrentByArtifact(@PathVariable Long artifactId) {
        Loan loan = loanService.getCurrentLoanByArtifactId(artifactId);
        return Result.success(loan);
    }

    @PostMapping("/approve")
    public Result<Void> approve(@RequestBody Map<String, String> params) {
        Long loanId = Long.parseLong(params.get("loanId"));
        String stage = params.get("stage");
        String approverName = params.get("approverName");
        String approverRole = params.get("approverRole");
        String opinion = params.get("opinion");
        String status = params.get("status");
        loanService.approve(loanId, stage, approverName, approverRole, opinion, status);
        return Result.success();
    }

    @GetMapping("/{id}/flow")
    public Result<List<LoanFlow>> getFlowHistory(@PathVariable Long id) {
        return Result.success(loanService.getFlowHistory(id));
    }
}
