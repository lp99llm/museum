package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.LoanQueryDTO;
import com.museum.museumsystem.dto.request.LoanReturnDTO;
import com.museum.museumsystem.entity.Loan;
import com.museum.museumsystem.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
        loan.setCreatedBy(1L); // 当前用户ID
        loan.setStatus("LOANED");
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

    // 归还文物
    @PostMapping("/return")
    public Result<Void> returnLoan(@RequestBody @Valid LoanReturnDTO returnDTO) {
        loanService.returnLoan(returnDTO.getLoanId(), returnDTO.getActualReturnDate());
        return Result.success();
    }

    // 查询文物当前借出记录
    @GetMapping("/artifact/{artifactId}/current")
    public Result<Loan> getCurrentByArtifact(@PathVariable Long artifactId) {
        Loan loan = loanService.getCurrentLoanByArtifactId(artifactId);
        return Result.success(loan);
    }
}