package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.RestorationQueryDTO;
import com.museum.museumsystem.entity.Restoration;
import com.museum.museumsystem.entity.RestorationFlow;
import com.museum.museumsystem.mapper.RestorationFlowMapper;
import com.museum.museumsystem.mapper.RestorationMapper;
import com.museum.museumsystem.service.RestorationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RestorationServiceImpl extends ServiceImpl<RestorationMapper, Restoration> implements RestorationService {

    @Autowired
    private RestorationFlowMapper restorationFlowMapper;

    @Override
    public PageResult<Restoration> pageQuery(RestorationQueryDTO queryDTO) {
        Page<Restoration> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        LambdaQueryWrapper<Restoration> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(queryDTO.getArtifactId() != null, Restoration::getArtifactId, queryDTO.getArtifactId())
                .like(queryDTO.getArtifactCode() != null, Restoration::getArtifactCode, queryDTO.getArtifactCode())
                .like(queryDTO.getArtifactName() != null, Restoration::getArtifactName, queryDTO.getArtifactName())
                .eq(queryDTO.getStatus() != null, Restoration::getStatus, queryDTO.getStatus())
                .eq(queryDTO.getCurrentStage() != null, Restoration::getCurrentStage, queryDTO.getCurrentStage())
                .eq(queryDTO.getRestorationType() != null, Restoration::getRestorationType, queryDTO.getRestorationType())
                .ge(queryDTO.getStartDate() != null, Restoration::getApplyDate, queryDTO.getStartDate())
                .le(queryDTO.getEndDate() != null, Restoration::getApplyDate, queryDTO.getEndDate())
                .orderByDesc(Restoration::getCreatedTime);

        Page<Restoration> pageResult = this.page(page, wrapper);
        PageResult<Restoration> result = new PageResult<>();
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setPages(pageResult.getPages());
        result.setRecords(pageResult.getRecords());
        return result;
    }

    @Override
    @Transactional
    public boolean approve(Long restorationId, String stage, String approverName, String approverRole, String opinion, String status) {
        Restoration restoration = this.getById(restorationId);
        if (restoration == null) {
            return false;
        }

        RestorationFlow flow = new RestorationFlow();
        flow.setRestorationId(restorationId);
        flow.setStageName(stage);
        flow.setApproverName(approverName);
        flow.setApproverRole(approverRole);
        flow.setApprovalOpinion(opinion);
        flow.setStatus(status);
        flow.setApproveTime(LocalDateTime.now());
        restorationFlowMapper.insert(flow);

        restoration.setCurrentStage(stage);
        if ("APPROVED".equals(status)) {
            if ("APPLY".equals(stage)) {
                restoration.setCurrentStage("PLAN");
            } else if ("PLAN".equals(stage)) {
                restoration.setCurrentStage("EXECUTE");
            } else if ("EXECUTE".equals(stage)) {
                restoration.setCurrentStage("ACCEPTANCE");
            } else if ("ACCEPTANCE".equals(stage)) {
                restoration.setCurrentStage("COMPLETED");
                restoration.setStatus("COMPLETED");
            }
        } else if ("REJECTED".equals(status)) {
            restoration.setStatus("REJECTED");
        }
        return this.updateById(restoration);
    }

    @Override
    public List<RestorationFlow> getFlowHistory(Long restorationId) {
        LambdaQueryWrapper<RestorationFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RestorationFlow::getRestorationId, restorationId)
                .orderByDesc(RestorationFlow::getCreatedTime);
        return restorationFlowMapper.selectList(wrapper);
    }
}
