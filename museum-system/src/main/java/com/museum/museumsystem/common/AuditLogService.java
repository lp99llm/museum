package com.museum.museumsystem.common;

import com.museum.museumsystem.entity.OperationLog;
import com.museum.museumsystem.mapper.OperationLogMapper;
import com.museum.museumsystem.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final OperationLogMapper operationLogMapper;

    public void logSuccess(String module, String type, String content) {
        save(module, type, content, "SUCCESS", null);
    }

    public void logFail(String module, String type, String content, String errorMsg) {
        save(module, type, content, "FAIL", errorMsg);
    }

    private void save(String module, String type, String content, String result, String errorMsg) {
        OperationLog log = new OperationLog();
        log.setOperator(getCurrentUserIdSafe());
        log.setOperationModule(module);
        log.setOperationType(type);
        log.setOperationDesc(content);
        log.setResult(result);
        log.setErrorMsg(errorMsg);
        log.setOperationTime(LocalDateTime.now());

        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            log.setRequestIp(resolveIp(request));
            log.setRequestUrl(request.getRequestURI());
            log.setRequestParams(request.getQueryString());
        }

        operationLogMapper.insert(log);
    }

    private Long getCurrentUserIdSafe() {
        try {
            return SecurityUtils.getCurrentUserId();
        } catch (Exception ex) {
            return null;
        }
    }

    private HttpServletRequest getCurrentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) attributes).getRequest();
        }
        return null;
    }

    private String resolveIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.trim().isEmpty()) {
            int commaIndex = forwarded.indexOf(',');
            return commaIndex > 0 ? forwarded.substring(0, commaIndex).trim() : forwarded.trim();
        }
        return request.getRemoteAddr();
    }
}
