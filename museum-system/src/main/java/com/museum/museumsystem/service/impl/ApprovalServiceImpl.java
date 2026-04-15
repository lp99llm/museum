package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.common.BusinessException;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.ApprovalQueryDTO;
import com.museum.museumsystem.entity.Approval;
import com.museum.museumsystem.mapper.ApprovalMapper;
import com.museum.museumsystem.service.ApprovalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ApprovalServiceImpl extends ServiceImpl<ApprovalMapper, Approval> implements ApprovalService {

    @Override
    public PageResult<Approval> pageQuery(ApprovalQueryDTO queryDTO) {
        Page<Approval> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        LambdaQueryWrapper<Approval> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getBizType() != null, Approval::getBizType, queryDTO.getBizType())
                .eq(queryDTO.getBizId() != null, Approval::getBizId, queryDTO.getBizId())
                .eq(queryDTO.getApprovalStatus() != null, Approval::getApprovalStatus, queryDTO.getApprovalStatus())
                .eq(queryDTO.getApplicant() != null, Approval::getApplicant, queryDTO.getApplicant())
                .orderByDesc(Approval::getApplyTime);
        Page<Approval> pageResult = this.page(page, wrapper);
        PageResult<Approval> result = new PageResult<>();
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setPages(pageResult.getPages());
        result.setRecords(pageResult.getRecords());
        return result;
    }

    @Override
    @Transactional
    public Approval submitApproval(String bizType, Long bizId, Long applicant, String currentNode) {
        Approval approval = new Approval();
        approval.setBizType(bizType);
        approval.setBizId(bizId);
        approval.setApprovalStatus("PENDING");
        approval.setApplicant(applicant);
        approval.setApplyTime(LocalDateTime.now());
        approval.setCurrentNode(currentNode);
        this.save(approval);
        return approval;
    }

    @Override
    @Transactional
    public boolean processApproval(Long approvalId, String approvalStatus, Long approver, String approveOpinion) {
        Approval approval = this.getById(approvalId);
        if (approval == null) {
            throw new BusinessException("审批记录不存在");
        }
        if (!"PENDING".equals(approval.getApprovalStatus())) {
            throw new BusinessException("该审批已处理，不能重复操作");
        }
        approval.setApprovalStatus(approvalStatus);
        approval.setApprover(approver);
        approval.setApproveTime(LocalDateTime.now());
        approval.setApproveOpinion(approveOpinion);
        return this.updateById(approval);
    }
}