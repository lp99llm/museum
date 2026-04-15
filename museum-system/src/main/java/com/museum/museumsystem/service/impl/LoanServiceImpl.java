package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.common.BusinessException;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.LoanQueryDTO;
import com.museum.museumsystem.entity.Loan;
import com.museum.museumsystem.mapper.LoanMapper;
import com.museum.museumsystem.service.LoanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class LoanServiceImpl extends ServiceImpl<LoanMapper, Loan> implements LoanService {

    @Override
    public PageResult<Loan> pageQuery(LoanQueryDTO queryDTO) {
        Page<Loan> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        LambdaQueryWrapper<Loan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getArtifactId() != null, Loan::getArtifactId, queryDTO.getArtifactId())
                .eq(queryDTO.getStatus() != null, Loan::getStatus, queryDTO.getStatus())
                .orderByDesc(Loan::getCreatedTime);
        Page<Loan> pageResult = this.page(page, wrapper);
        PageResult<Loan> result = new PageResult<>();
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setPages(pageResult.getPages());
        result.setRecords(pageResult.getRecords());
        return result;
    }

    @Override
    @Transactional
    public boolean returnLoan(Long loanId, LocalDate actualReturnDate) {
        Loan loan = this.getById(loanId);
        if (loan == null) {
            throw new BusinessException("借出记录不存在");
        }
        if (!"LOANED".equals(loan.getStatus())) {
            throw new BusinessException("该记录已归还，不能重复归还");
        }
        loan.setActualReturnDate(actualReturnDate);
        loan.setStatus("RETURNED");
        return this.updateById(loan);
    }

    @Override
    public Loan getCurrentLoanByArtifactId(Long artifactId) {
        LambdaQueryWrapper<Loan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Loan::getArtifactId, artifactId)
                .eq(Loan::getStatus, "LOANED")
                .orderByDesc(Loan::getLoanDate)
                .last("LIMIT 1");
        return this.getOne(wrapper);
    }
}