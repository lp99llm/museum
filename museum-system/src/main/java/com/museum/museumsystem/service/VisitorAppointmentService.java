package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.VisitorAppointmentDTO;
import com.museum.museumsystem.dto.request.VisitorAppointmentQueryDTO;
import com.museum.museumsystem.entity.VisitorAppointment;

public interface VisitorAppointmentService extends IService<VisitorAppointment> {
    PageResult<VisitorAppointmentDTO> pageQuery(VisitorAppointmentQueryDTO queryDTO);

    boolean approve(Long id);

    boolean reject(Long id, String reason);

    boolean cancel(Long id);
}
