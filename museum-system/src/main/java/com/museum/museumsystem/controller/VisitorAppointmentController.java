package com.museum.museumsystem.controller;

import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.VisitorAppointmentDTO;
import com.museum.museumsystem.dto.request.VisitorAppointmentQueryDTO;
import com.museum.museumsystem.entity.VisitorAppointment;
import com.museum.museumsystem.security.SecurityUtils;
import com.museum.museumsystem.service.VisitorAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/visitor-appointment")
@RequiredArgsConstructor
public class VisitorAppointmentController {

    private final VisitorAppointmentService visitorAppointmentService;

    @PostMapping("/page")
    @PreAuthorize("hasAuthority('visitor-appointment:view')")
    public Result<PageResult<VisitorAppointmentDTO>> page(@RequestBody VisitorAppointmentQueryDTO queryDTO) {
        return Result.success(visitorAppointmentService.pageQuery(queryDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('visitor-appointment:view')")
    public Result<VisitorAppointment> getById(@PathVariable Long id) {
        return Result.success(visitorAppointmentService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('visitor-appointment:add')")
    public Result<Void> add(@RequestBody VisitorAppointment appointment) {
        appointment.setCreatedBy(SecurityUtils.getCurrentUserId());
        appointment.setStatus("PENDING");
        visitorAppointmentService.save(appointment);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('visitor-appointment:edit')")
    public Result<Void> update(@RequestBody VisitorAppointment appointment) {
        visitorAppointmentService.updateById(appointment);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('visitor-appointment:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        visitorAppointmentService.removeById(id);
        return Result.success();
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('visitor-appointment:approve')")
    public Result<Void> approve(@PathVariable Long id) {
        visitorAppointmentService.approve(id);
        return Result.success();
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('visitor-appointment:approve')")
    public Result<Void> reject(@PathVariable Long id, @RequestBody String reason) {
        visitorAppointmentService.reject(id, reason);
        return Result.success();
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('visitor-appointment:edit')")
    public Result<Void> cancel(@PathVariable Long id) {
        visitorAppointmentService.cancel(id);
        return Result.success();
    }
}
