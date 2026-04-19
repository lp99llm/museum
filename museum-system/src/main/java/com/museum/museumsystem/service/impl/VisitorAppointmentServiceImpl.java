package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.VisitorAppointmentDTO;
import com.museum.museumsystem.dto.request.VisitorAppointmentQueryDTO;
import com.museum.museumsystem.entity.Exhibition;
import com.museum.museumsystem.entity.VisitorAppointment;
import com.museum.museumsystem.mapper.ExhibitionMapper;
import com.museum.museumsystem.mapper.VisitorAppointmentMapper;
import com.museum.museumsystem.service.VisitorAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitorAppointmentServiceImpl extends ServiceImpl<VisitorAppointmentMapper, VisitorAppointment>
        implements VisitorAppointmentService {

    private final ExhibitionMapper exhibitionMapper;

    @Override
    public PageResult<VisitorAppointmentDTO> pageQuery(VisitorAppointmentQueryDTO queryDTO) {
        Page<VisitorAppointment> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        LambdaQueryWrapper<VisitorAppointment> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(StringUtils.hasText(queryDTO.getVisitorName()), VisitorAppointment::getVisitorName, queryDTO.getVisitorName())
                .like(StringUtils.hasText(queryDTO.getVisitorPhone()), VisitorAppointment::getVisitorPhone, queryDTO.getVisitorPhone())
                .eq(queryDTO.getExhibitionId() != null, VisitorAppointment::getExhibitionId, queryDTO.getExhibitionId())
                .eq(StringUtils.hasText(queryDTO.getStatus()), VisitorAppointment::getStatus, queryDTO.getStatus())
                .ge(queryDTO.getStartDate() != null, VisitorAppointment::getAppointmentDate, queryDTO.getStartDate())
                .le(queryDTO.getEndDate() != null, VisitorAppointment::getAppointmentDate, queryDTO.getEndDate())
                .orderByDesc(VisitorAppointment::getCreatedTime);

        Page<VisitorAppointment> pageResult = this.page(page, wrapper);
        List<VisitorAppointment> records = pageResult.getRecords();

        List<Long> exhibitionIds = records.stream().map(VisitorAppointment::getExhibitionId).distinct().collect(Collectors.toList());
        Map<Long, String> exhibitionMap = new HashMap<>();
        if (!exhibitionIds.isEmpty()) {
            List<Exhibition> exhibitions = exhibitionMapper.selectBatchIds(exhibitionIds);
            exhibitions.forEach(e -> exhibitionMap.put(e.getId(), e.getName()));
        }

        List<VisitorAppointmentDTO> dtoList = records.stream().map(r -> {
            VisitorAppointmentDTO dto = new VisitorAppointmentDTO();
            dto.setId(r.getId());
            dto.setExhibitionId(r.getExhibitionId());
            dto.setExhibitionName(exhibitionMap.get(r.getExhibitionId()));
            dto.setVisitorName(r.getVisitorName());
            dto.setVisitorPhone(r.getVisitorPhone());
            dto.setVisitorIdCard(r.getVisitorIdCard());
            dto.setAppointmentDate(r.getAppointmentDate());
            dto.setAppointmentTimeSlot(r.getAppointmentTimeSlot());
            dto.setVisitorCount(r.getVisitorCount());
            dto.setStatus(r.getStatus());
            dto.setRemark(r.getRemark());
            return dto;
        }).collect(Collectors.toList());

        PageResult<VisitorAppointmentDTO> result = new PageResult<>();
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setPages(pageResult.getPages());
        result.setRecords(dtoList);
        return result;
    }

    @Override
    @Transactional
    public boolean approve(Long id) {
        VisitorAppointment appointment = this.getById(id);
        if (appointment == null) {
            return false;
        }
        appointment.setStatus("APPROVED");
        return this.updateById(appointment);
    }

    @Override
    @Transactional
    public boolean reject(Long id, String reason) {
        VisitorAppointment appointment = this.getById(id);
        if (appointment == null) {
            return false;
        }
        appointment.setStatus("REJECTED");
        appointment.setRemark(reason);
        return this.updateById(appointment);
    }

    @Override
    @Transactional
    public boolean cancel(Long id) {
        VisitorAppointment appointment = this.getById(id);
        if (appointment == null) {
            return false;
        }
        appointment.setStatus("CANCELLED");
        return this.updateById(appointment);
    }
}
