package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.LoanQueryDTO;
import com.museum.museumsystem.entity.Loan;
import com.museum.museumsystem.entity.LoanFlow;

import java.time.LocalDate;
import java.util.List;

public interface LoanService extends IService<Loan> {
    PageResult<Loan> pageQuery(LoanQueryDTO queryDTO);

    boolean returnLoan(Long loanId, LocalDate actualReturnDate);

    Loan getCurrentLoanByArtifactId(Long artifactId);

    boolean approve(Long loanId, String stage, String approverName, String approverRole, String opinion, String status);

    List<LoanFlow> getFlowHistory(Long loanId);
}
