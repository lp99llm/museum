package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.LoanQueryDTO;
import com.museum.museumsystem.entity.Loan;
import java.time.LocalDate;

public interface LoanService extends IService<Loan> {
    PageResult<Loan> pageQuery(LoanQueryDTO queryDTO);

    // 归还文物
    boolean returnLoan(Long loanId, LocalDate actualReturnDate);

    // 查询某个文物的借出记录（可选）
    Loan getCurrentLoanByArtifactId(Long artifactId);
}