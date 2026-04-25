package com.museum.museumsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.entity.OperationLog;
import com.museum.museumsystem.entity.User;
import com.museum.museumsystem.mapper.OperationLogMapper;
import com.museum.museumsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/operation-log")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogMapper operationLogMapper;
    private final UserService userService;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('operation-log:view')")
    public Result<PageResult<Map<String, Object>>> page(@RequestParam(defaultValue = "1") Long current,
                                                        @RequestParam(defaultValue = "10") Long size,
                                                        @RequestParam(required = false) String operator,
                                                        @RequestParam(required = false) String type,
                                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Page<OperationLog> page = new Page<>(current, size);
        LambdaQueryWrapper<OperationLog> wrapper = buildWrapper(operator, type, startDate, endDate);
        if (wrapper == null) {
            return Result.success(new PageResult<>(current, size, 0, 0, Collections.emptyList()));
        }

        wrapper.orderByDesc(OperationLog::getOperationTime);
        Page<OperationLog> pageResult = operationLogMapper.selectPage(page, wrapper);
        Map<Long, User> userMap = buildUserMap(pageResult.getRecords());

        List<Map<String, Object>> records = pageResult.getRecords().stream()
                .map(log -> toView(log, userMap.get(log.getOperator())))
                .collect(Collectors.toList());

        PageResult<Map<String, Object>> result = new PageResult<>(
                pageResult.getCurrent(),
                pageResult.getSize(),
                pageResult.getTotal(),
                pageResult.getPages(),
                records
        );
        return Result.success(result);
    }

    @GetMapping("/export")
    @PreAuthorize("hasAuthority('report:export')")
    public void export(@RequestParam(required = false) String operator,
                       @RequestParam(required = false) String type,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                       HttpServletResponse response) throws IOException {
        LambdaQueryWrapper<OperationLog> wrapper = buildWrapper(operator, type, startDate, endDate);
        List<OperationLog> logs = wrapper == null
                ? Collections.emptyList()
                : operationLogMapper.selectList(wrapper.orderByDesc(OperationLog::getOperationTime));
        Map<Long, User> userMap = buildUserMap(logs);

        StringBuilder csv = new StringBuilder();
        csv.append('\ufeff');
        csv.append("操作时间,操作人,用户名,操作类型,操作模块,操作内容,结果,IP地址,请求地址,执行耗时(ms),错误信息\n");

        for (OperationLog log : logs) {
            Map<String, Object> item = toView(log, userMap.get(log.getOperator()));
            csv.append(csvValue(item.get("time"))).append(',')
                    .append(csvValue(item.get("operatorName"))).append(',')
                    .append(csvValue(item.get("username"))).append(',')
                    .append(csvValue(item.get("type"))).append(',')
                    .append(csvValue(item.get("module"))).append(',')
                    .append(csvValue(item.get("content"))).append(',')
                    .append(csvValue(item.get("result"))).append(',')
                    .append(csvValue(item.get("ip"))).append(',')
                    .append(csvValue(item.get("url"))).append(',')
                    .append(csvValue(item.get("duration"))).append(',')
                    .append(csvValue(item.get("errorMsg")))
                    .append('\n');
        }

        String fileName = URLEncoder.encode("operation-log-export.csv", StandardCharsets.UTF_8.name());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);
        response.getWriter().write(csv.toString());
        response.getWriter().flush();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('operation-log:view')")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        OperationLog log = operationLogMapper.selectById(id);
        if (log == null) {
            return Result.error("日志不存在");
        }
        User user = log.getOperator() == null ? null : userService.getById(log.getOperator());
        return Result.success(toView(log, user));
    }

    private LambdaQueryWrapper<OperationLog> buildWrapper(String operator,
                                                          String type,
                                                          LocalDate startDate,
                                                          LocalDate endDate) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();

        if (type != null && !type.trim().isEmpty()) {
            wrapper.eq(OperationLog::getOperationType, type.trim());
        }

        if (startDate != null) {
            wrapper.ge(OperationLog::getOperationTime, startDate.atStartOfDay());
        }

        if (endDate != null) {
            wrapper.le(OperationLog::getOperationTime, endDate.atTime(23, 59, 59));
        }

        if (operator != null && !operator.trim().isEmpty()) {
            List<Long> userIds = userService.lambdaQuery()
                    .and(query -> query.like(User::getUsername, operator.trim())
                            .or()
                            .like(User::getRealName, operator.trim()))
                    .list()
                    .stream()
                    .map(User::getId)
                    .collect(Collectors.toList());

            if (userIds.isEmpty()) {
                return null;
            }

            wrapper.in(OperationLog::getOperator, userIds);
        }

        return wrapper;
    }

    private Map<Long, User> buildUserMap(List<OperationLog> logs) {
        Set<Long> operatorIds = logs.stream()
                .map(OperationLog::getOperator)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return operatorIds.isEmpty()
                ? Collections.emptyMap()
                : userService.listByIds(operatorIds).stream().collect(Collectors.toMap(User::getId, user -> user));
    }

    private Map<String, Object> toView(OperationLog log, User user) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", log.getId());
        item.put("operatorId", log.getOperator());
        item.put("operatorName", user != null
                ? (user.getRealName() != null && !user.getRealName().isEmpty() ? user.getRealName() : user.getUsername())
                : String.valueOf(log.getOperator()));
        item.put("username", user != null ? user.getUsername() : "-");
        item.put("time", formatTime(log.getOperationTime()));
        item.put("type", log.getOperationType());
        item.put("module", log.getOperationModule());
        item.put("content", log.getOperationDesc());
        item.put("ip", log.getRequestIp());
        item.put("url", log.getRequestUrl());
        item.put("requestParams", safe(log.getRequestParams()));
        item.put("result", log.getResult());
        item.put("errorMsg", safe(log.getErrorMsg()));
        item.put("duration", log.getDuration());
        return item;
    }

    private String formatTime(LocalDateTime time) {
        return time == null ? "-" : time.toString().replace('T', ' ');
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    private String csvValue(Object value) {
        String text = value == null ? "-" : String.valueOf(value);
        String escaped = text.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}
