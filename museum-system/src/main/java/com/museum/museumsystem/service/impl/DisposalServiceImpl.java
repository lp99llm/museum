package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.DisposalQueryDTO;
import com.museum.museumsystem.entity.Disposal;
import com.museum.museumsystem.entity.DisposalFlow;
import com.museum.museumsystem.mapper.DisposalFlowMapper;
import com.museum.museumsystem.mapper.DisposalMapper;
import com.museum.museumsystem.service.DisposalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DisposalServiceImpl extends ServiceImpl<DisposalMapper, Disposal> implements DisposalService {

    @Autowired
    private DisposalFlowMapper disposalFlowMapper;

    @Override
    public PageResult<Disposal> pageQuery(DisposalQueryDTO queryDTO) {
        Page<Disposal> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        LambdaQueryWrapper<Disposal> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(queryDTO.getArtifactId() != null, Disposal::getArtifactId, queryDTO.getArtifactId())
                .like(queryDTO.getArtifactCode() != null, Disposal::getArtifactCode, queryDTO.getArtifactCode())
                .like(queryDTO.getArtifactName() != null, Disposal::getArtifactName, queryDTO.getArtifactName())
                .eq(queryDTO.getDisposalType() != null, Disposal::getDisposalType, queryDTO.getDisposalType())
                .eq(queryDTO.getStatus() != null, Disposal::getStatus, queryDTO.getStatus())
                .eq(queryDTO.getCurrentStage() != null, Disposal::getCurrentStage, queryDTO.getCurrentStage())
                .like(queryDTO.getApplicant() != null, Disposal::getApplicant, queryDTO.getApplicant())
                .ge(queryDTO.getStartDate() != null, Disposal::getApplyDate, queryDTO.getStartDate())
                .le(queryDTO.getEndDate() != null, Disposal::getApplyDate, queryDTO.getEndDate())
                .orderByDesc(Disposal::getCreatedTime);

        Page<Disposal> pageResult = this.page(page, wrapper);
        PageResult<Disposal> result = new PageResult<>();
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setPages(pageResult.getPages());
        result.setRecords(pageResult.getRecords());
        return result;
    }

    @Override
    @Transactional
    public boolean approve(Long disposalId, String stage, String approverName, String approverRole, String opinion, String status) {
        Disposal disposal = this.getById(disposalId);
        if (disposal == null) {
            return false;
        }

        DisposalFlow flow = new DisposalFlow();
        flow.setDisposalId(disposalId);
        flow.setStageName(stage);
        flow.setApproverName(approverName);
        flow.setApproverRole(approverRole);
        flow.setApprovalOpinion(opinion);
        flow.setStatus(status);
        flow.setApproveTime(LocalDateTime.now());
        disposalFlowMapper.insert(flow);

        disposal.setCurrentStage(stage);
        if ("APPROVED".equals(status)) {
            if ("APPLY".equals(stage)) {
                disposal.setCurrentStage("EVALUATION");
            } else if ("EVALUATION".equals(stage)) {
                disposal.setCurrentStage("PUBLIC");
            } else if ("PUBLIC".equals(stage)) {
                disposal.setCurrentStage("RECORD");
            } else if ("RECORD".equals(stage)) {
                disposal.setCurrentStage("EXECUTION");
                disposal.setStatus("APPROVED");
                disposal.setApprovalDate(LocalDate.now());
                disposal.setApprover(approverName);
                disposal.setApprovalOpinion(opinion);
            } else if ("EXECUTION".equals(stage)) {
                disposal.setCurrentStage("ARCHIVE");
                disposal.setDisposalDate(LocalDate.now());
                disposal.setExecutionResult("SUCCESS");
            } else if ("ARCHIVE".equals(stage)) {
                disposal.setCurrentStage("COMPLETED");
                disposal.setArchiveDate(LocalDate.now());
            }
        } else if ("REJECTED".equals(status)) {
            disposal.setStatus("REJECTED");
        }
        return this.updateById(disposal);
    }

    @Override
    public List<DisposalFlow> getFlowHistory(Long disposalId) {
        LambdaQueryWrapper<DisposalFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DisposalFlow::getDisposalId, disposalId)
                .orderByDesc(DisposalFlow::getCreatedTime);
        return disposalFlowMapper.selectList(wrapper);
    }
}
