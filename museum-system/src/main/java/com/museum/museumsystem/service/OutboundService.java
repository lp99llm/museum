package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.OutboundQueryDTO;
import com.museum.museumsystem.entity.Outbound;
import com.museum.museumsystem.entity.OutboundFlow;

import java.util.List;

public interface OutboundService extends IService<Outbound> {
    PageResult<Outbound> pageQuery(OutboundQueryDTO queryDTO);

    boolean approve(Long outboundId, String stage, String approverName, String approverRole, String opinion, String status);

    boolean returnArtifact(Long outboundId, String returnImage, String returnRemarks);

    List<OutboundFlow> getFlowHistory(Long outboundId);
}
