package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.LoanQueryDTO;
import com.museum.museumsystem.dto.request.LoanReturnDTO;
import com.museum.museumsystem.entity.Loan;
import com.museum.museumsystem.entity.LoanFlow;
import com.museum.museumsystem.security.SecurityUtils;
import com.museum.museumsystem.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('loan:view')")
    public Result<PageResult<Loan>> page(@RequestBody @Valid LoanQueryDTO queryDTO) {
        return Result.success(loanService.pageQuery(queryDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('loan:view')")
    public Result<Loan> getById(@PathVariable Long id) {
        return Result.success(loanService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('loan:add')")
    public Result<Void> add(@RequestBody @Valid Loan loan) {
        loan.setCreatedBy(SecurityUtils.getCurrentUserId());
        loan.setStatus("PENDING");
        loan.setCurrentStage("APPLY");
        loanService.save(loan);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('loan:edit')")
    public Result<Void> update(@RequestBody @Valid Loan loan) {
        loanService.updateById(loan);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('loan:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        loanService.removeById(id);
        return Result.success();
    }

    @PostMapping("/return")
    @PreAuthorize("hasAuthority('loan:return')")
    public Result<Void> returnLoan(@RequestBody @Valid LoanReturnDTO returnDTO) {
        loanService.returnLoan(returnDTO.getLoanId(), returnDTO.getActualReturnDate());
        return Result.success();
    }

    @GetMapping("/artifact/{artifactId}/current")
    @PreAuthorize("hasAuthority('loan:view')")
    public Result<Loan> getCurrentByArtifact(@PathVariable Long artifactId) {
        Loan loan = loanService.getCurrentLoanByArtifactId(artifactId);
        return Result.success(loan);
    }

    @PostMapping("/approve")
    @PreAuthorize("hasAuthority('loan:approve')")
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
    @PreAuthorize("hasAuthority('loan:view')")
    public Result<List<LoanFlow>> getFlowHistory(@PathVariable Long id) {
        return Result.success(loanService.getFlowHistory(id));
    }
}
