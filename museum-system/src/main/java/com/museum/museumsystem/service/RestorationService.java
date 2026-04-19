package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.RestorationQueryDTO;
import com.museum.museumsystem.entity.Restoration;
import com.museum.museumsystem.entity.RestorationFlow;

import java.util.List;

public interface RestorationService extends IService<Restoration> {
    PageResult<Restoration> pageQuery(RestorationQueryDTO queryDTO);

    boolean approve(Long restorationId, String stage, String approverName, String approverRole, String opinion, String status);

    List<RestorationFlow> getFlowHistory(Long restorationId);
}
