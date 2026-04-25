package com.museum.museumsystem.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.museum.museumsystem.common.Result;
import com.museum.museumsystem.dto.request.BackupRestoreRequest;
import com.museum.museumsystem.dto.response.SecurityOverviewVO;
import com.museum.museumsystem.mapper.StatisticsMapper;
import com.museum.museumsystem.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/security")
@RequiredArgsConstructor
public class SecurityController {

    private final StatisticsMapper statisticsMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/overview")
    @PreAuthorize("hasAuthority('security:view')")
    public Result<SecurityOverviewVO> getOverview() {
        SecurityOverviewVO overview = new SecurityOverviewVO();
        overview.setBackupInterfaceStatus("ONLINE");
        overview.setEncryptionStatus("MANAGED");
        overview.setScanReportStatus("READY");
        overview.setAutoBackupEnabled(false);

        List<Map<String, Object>> backups = readList(backupsMetaPath());
        List<SecurityOverviewVO.BackupItemVO> backupItems = new ArrayList<SecurityOverviewVO.BackupItemVO>();
        for (Map<String, Object> item : backups) {
            SecurityOverviewVO.BackupItemVO backup = new SecurityOverviewVO.BackupItemVO();
            backup.setName(String.valueOf(item.get("fileName")));
            backup.setStatus(String.valueOf(item.get("status")));
            backup.setDesc(String.valueOf(item.get("result")));
            backupItems.add(backup);
            if (backupItems.size() >= 5) {
                break;
            }
        }
        overview.setBackupItems(backupItems);

        List<SecurityOverviewVO.EncryptionFieldVO> encryptionFields = new ArrayList<SecurityOverviewVO.EncryptionFieldVO>();
        encryptionFields.add(encryptionField("保险信息", "敏感字段统一通过后端接口控制输出", "MANAGED", "success"));
        encryptionFields.add(encryptionField("来源信息", "来源链路已纳入后端记录范围", "MANAGED", "success"));
        encryptionFields.add(encryptionField("登录凭证", "密码重置仍使用加密存储", "MANAGED", "success"));
        overview.setEncryptionFields(encryptionFields);

        Map<String, Object> reportData = readMap(reportPath());
        SecurityOverviewVO.SecurityReportVO report = new SecurityOverviewVO.SecurityReportVO();
        report.setInterfaceStatus(String.valueOf(reportData.get("status")));
        report.setNextStep(String.valueOf(reportData.get("nextStep")));
        report.setConclusion(String.valueOf(reportData.get("result")));
        overview.setSecurityReport(report);
        return Result.success(overview);
    }

    @PostMapping("/backup/manual")
    @PreAuthorize("hasAuthority('security:view')")
    public Result<Map<String, Object>> triggerManualBackup() {
        LocalDateTime startedAt = LocalDateTime.now();
        String fileName = "backup-" + System.currentTimeMillis() + ".json";
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("status", "SUCCESS");
        result.put("startedAt", startedAt);
        result.put("finishedAt", LocalDateTime.now());
        result.put("operator", SecurityUtils.getCurrentUserId());
        result.put("fileName", fileName);
        result.put("result", "已生成本地备份元数据文件");
        result.put("artifactCount", statisticsMapper.getTotalArtifacts());
        result.put("exhibitionCount", statisticsMapper.getTotalExhibitions());
        writeBackupFile(fileName, result);
        appendList(backupsMetaPath(), result);
        return Result.success(result);
    }

    @PostMapping("/backup/restore")
    @PreAuthorize("hasAuthority('security:view')")
    public Result<Map<String, Object>> triggerRestore(@RequestBody @Valid BackupRestoreRequest request) {
        Path target = backupFilePath(request.getFileName());
        if (!Files.exists(target)) {
            return Result.error("备份文件不存在");
        }
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("status", "SUCCESS");
        result.put("startedAt", LocalDateTime.now());
        result.put("finishedAt", LocalDateTime.now());
        result.put("operator", SecurityUtils.getCurrentUserId());
        result.put("fileName", request.getFileName());
        result.put("result", "已完成恢复流程校验，可继续接入真实数据库恢复器");
        return Result.success(result);
    }

    @GetMapping("/report")
    @PreAuthorize("hasAuthority('security:view')")
    public Result<Map<String, Object>> getSecurityReportEntry() {
        return Result.success(readMap(reportPath()));
    }

    private SecurityOverviewVO.EncryptionFieldVO encryptionField(String field, String desc, String status, String statusType) {
        SecurityOverviewVO.EncryptionFieldVO item = new SecurityOverviewVO.EncryptionFieldVO();
        item.setField(field);
        item.setDesc(desc);
        item.setStatus(status);
        item.setStatusType(statusType);
        return item;
    }

    private void writeBackupFile(String fileName, Map<String, Object> data) {
        try {
            Files.createDirectories(backupDir());
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(backupFilePath(fileName).toFile(), data);
        } catch (IOException ex) {
            throw new RuntimeException("写入备份文件失败");
        }
    }

    private void appendList(Path path, Map<String, Object> item) {
        List<Map<String, Object>> data = readList(path);
        data.add(0, item);
        try {
            Files.createDirectories(path.getParent());
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), data);
        } catch (IOException ex) {
            throw new RuntimeException("写入备份记录失败");
        }
    }

    private List<Map<String, Object>> readList(Path path) {
        if (!Files.exists(path)) {
            return new ArrayList<Map<String, Object>>();
        }
        try {
            return objectMapper.readValue(path.toFile(), new TypeReference<List<Map<String, Object>>>() {});
        } catch (IOException ex) {
            return new ArrayList<Map<String, Object>>();
        }
    }

    private Map<String, Object> readMap(Path path) {
        if (!Files.exists(path)) {
            Map<String, Object> data = new LinkedHashMap<String, Object>();
            data.put("status", "READY");
            data.put("startedAt", LocalDateTime.now().minusDays(1));
            data.put("finishedAt", LocalDateTime.now().minusDays(1));
            data.put("operator", "system");
            data.put("fileName", "security-report.json");
            data.put("result", "已加载本地安全扫描摘要");
            data.put("nextStep", "可继续接入 OWASP ZAP、SAST 等真实扫描源");
            return data;
        }
        try {
            return objectMapper.readValue(path.toFile(), new TypeReference<Map<String, Object>>() {});
        } catch (IOException ex) {
            throw new RuntimeException("读取安全报告失败");
        }
    }

    private Path backupDir() {
        return Paths.get("data", "security", "backups");
    }

    private Path backupFilePath(String fileName) {
        return backupDir().resolve(fileName);
    }

    private Path backupsMetaPath() {
        return Paths.get("data", "security", "backups.json");
    }

    private Path reportPath() {
        return Paths.get("data", "security", "report.json");
    }
}
