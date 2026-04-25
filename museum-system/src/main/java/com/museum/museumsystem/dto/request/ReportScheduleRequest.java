package com.museum.museumsystem.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class ReportScheduleRequest {
    @NotBlank(message = "任务名称不能为空")
    private String name;
    @NotBlank(message = "cron不能为空")
    private String cron;
    @NotEmpty(message = "指标列表不能为空")
    private List<String> metrics;
    private String receiver;
}
