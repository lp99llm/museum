package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.DisposalQueryDTO;
import com.museum.museumsystem.entity.Disposal;
import com.museum.museumsystem.entity.DisposalFlow;

import java.util.List;

public interface DisposalService extends IService<Disposal> {
    PageResult<Disposal> pageQuery(DisposalQueryDTO queryDTO);

    boolean approve(Long disposalId, String stage, String approverName, String approverRole, String opinion, String status);

    List<DisposalFlow> getFlowHistory(Long disposalId);
}
