package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.OutboundQueryDTO;
import com.museum.museumsystem.entity.Outbound;
import com.museum.museumsystem.entity.OutboundFlow;
import com.museum.museumsystem.mapper.OutboundFlowMapper;
import com.museum.museumsystem.mapper.OutboundMapper;
import com.museum.museumsystem.service.OutboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OutboundServiceImpl extends ServiceImpl<OutboundMapper, Outbound> implements OutboundService {

    @Autowired
    private OutboundFlowMapper outboundFlowMapper;

    @Override
    public PageResult<Outbound> pageQuery(OutboundQueryDTO queryDTO) {
        Page<Outbound> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        LambdaQueryWrapper<Outbound> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(queryDTO.getArtifactId() != null, Outbound::getArtifactId, queryDTO.getArtifactId())
                .like(queryDTO.getArtifactCode() != null, Outbound::getArtifactCode, queryDTO.getArtifactCode())
                .like(queryDTO.getArtifactName() != null, Outbound::getArtifactName, queryDTO.getArtifactName())
                .eq(queryDTO.getStatus() != null, Outbound::getStatus, queryDTO.getStatus())
                .eq(queryDTO.getCurrentStage() != null, Outbound::getCurrentStage, queryDTO.getCurrentStage())
                .ge(queryDTO.getStartDate() != null, Outbound::getOutboundDate, queryDTO.getStartDate())
                .le(queryDTO.getEndDate() != null, Outbound::getOutboundDate, queryDTO.getEndDate())
                .orderByDesc(Outbound::getCreatedTime);

        Page<Outbound> pageResult = this.page(page, wrapper);
        PageResult<Outbound> result = new PageResult<>();
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setPages(pageResult.getPages());
        result.setRecords(pageResult.getRecords());
        return result;
    }

    @Override
    @Transactional
    public boolean approve(Long outboundId, String stage, String approverName, String approverRole, String opinion, String status) {
        Outbound outbound = this.getById(outboundId);
        if (outbound == null) {
            return false;
        }

        OutboundFlow flow = new OutboundFlow();
        flow.setOutboundId(outboundId);
        flow.setStageName(stage);
        flow.setApproverName(approverName);
        flow.setApproverRole(approverRole);
        flow.setApprovalOpinion(opinion);
        flow.setStatus(status);
        flow.setApproveTime(LocalDateTime.now());
        outboundFlowMapper.insert(flow);

        outbound.setCurrentStage(stage);
        if ("APPROVED".equals(status)) {
            if ("STAGE1".equals(stage)) {
                outbound.setCurrentStage("STAGE2");
            } else if ("STAGE2".equals(stage)) {
                outbound.setCurrentStage("STAGE3");
            } else if ("STAGE3".equals(stage)) {
                outbound.setCurrentStage("APPROVED");
                outbound.setStatus("APPROVED");
                outbound.setOutboundDate(LocalDate.now());
            }
        } else if ("REJECTED".equals(status)) {
            outbound.setStatus("REJECTED");
        }
        return this.updateById(outbound);
    }

    @Override
    @Transactional
    public boolean returnArtifact(Long outboundId, String returnImage, String returnRemarks) {
        Outbound outbound = this.getById(outboundId);
        if (outbound == null) {
            return false;
        }
        outbound.setActualReturnDate(LocalDate.now());
        outbound.setReturnImage(returnImage);
        outbound.setReturnRemarks(returnRemarks);
        outbound.setStatus("RETURNED");
        outbound.setCurrentStage("RETURNED");
        return this.updateById(outbound);
    }

    @Override
    public List<OutboundFlow> getFlowHistory(Long outboundId) {
        LambdaQueryWrapper<OutboundFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OutboundFlow::getOutboundId, outboundId)
                .orderByDesc(OutboundFlow::getCreatedTime);
        return outboundFlowMapper.selectList(wrapper);
    }
}
