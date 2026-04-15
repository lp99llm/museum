package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.ApprovalQueryDTO;
import com.museum.museumsystem.entity.Approval;

public interface ApprovalService extends IService<Approval> {
    PageResult<Approval> pageQuery(ApprovalQueryDTO queryDTO);

    // 提交审批
    Approval submitApproval(String bizType, Long bizId, Long applicant, String currentNode);

    // 审批通过/驳回
    boolean processApproval(Long approvalId, String approvalStatus, Long approver, String approveOpinion);
}