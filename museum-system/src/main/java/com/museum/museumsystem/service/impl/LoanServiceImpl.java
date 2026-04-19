package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.common.BusinessException;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.LoanQueryDTO;
import com.museum.museumsystem.entity.Loan;
import com.museum.museumsystem.entity.LoanFlow;
import com.museum.museumsystem.mapper.LoanFlowMapper;
import com.museum.museumsystem.mapper.LoanMapper;
import com.museum.museumsystem.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoanServiceImpl extends ServiceImpl<LoanMapper, Loan> implements LoanService {

    @Autowired
    private LoanFlowMapper loanFlowMapper;

    @Override
    public PageResult<Loan> pageQuery(LoanQueryDTO queryDTO) {
        Page<Loan> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        LambdaQueryWrapper<Loan> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(queryDTO.getArtifactId() != null, Loan::getArtifactId, queryDTO.getArtifactId())
                .like(queryDTO.getArtifactCode() != null, Loan::getArtifactCode, queryDTO.getArtifactCode())
                .like(queryDTO.getArtifactName() != null, Loan::getArtifactName, queryDTO.getArtifactName())
                .eq(queryDTO.getStatus() != null, Loan::getStatus, queryDTO.getStatus())
                .eq(queryDTO.getCurrentStage() != null, Loan::getCurrentStage, queryDTO.getCurrentStage())
                .like(queryDTO.getBorrowerInstitution() != null, Loan::getBorrowerInstitution, queryDTO.getBorrowerInstitution())
                .ge(queryDTO.getStartDate() != null, Loan::getApplyDate, queryDTO.getStartDate())
                .le(queryDTO.getEndDate() != null, Loan::getApplyDate, queryDTO.getEndDate())
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
        loan.setCurrentStage("RETURNED");
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

    @Override
    @Transactional
    public boolean approve(Long loanId, String stage, String approverName, String approverRole, String opinion, String status) {
        Loan loan = this.getById(loanId);
        if (loan == null) {
            return false;
        }

        LoanFlow flow = new LoanFlow();
        flow.setLoanId(loanId);
        flow.setStageName(stage);
        flow.setApproverName(approverName);
        flow.setApproverRole(approverRole);
        flow.setApprovalOpinion(opinion);
        flow.setStatus(status);
        flow.setApproveTime(LocalDateTime.now());
        loanFlowMapper.insert(flow);

        loan.setCurrentStage(stage);
        if ("APPROVED".equals(status)) {
            if ("APPLY".equals(stage)) {
                loan.setCurrentStage("AGREEMENT");
            } else if ("AGREEMENT".equals(stage)) {
                loan.setCurrentStage("LOANED");
                loan.setStatus("LOANED");
                loan.setLoanDate(LocalDate.now());
            }
        } else if ("REJECTED".equals(status)) {
            loan.setStatus("REJECTED");
        }
        return this.updateById(loan);
    }

    @Override
    public List<LoanFlow> getFlowHistory(Long loanId) {
        LambdaQueryWrapper<LoanFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoanFlow::getLoanId, loanId)
                .orderByDesc(LoanFlow::getCreatedTime);
        return loanFlowMapper.selectList(wrapper);
    }
}
