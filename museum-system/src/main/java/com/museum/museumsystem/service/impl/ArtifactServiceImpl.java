package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.common.BusinessException;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.ArtifactExportRequest;
import com.museum.museumsystem.dto.request.ArtifactImportRequest;
import com.museum.museumsystem.dto.request.ArtifactJoinExhibitionRequest;
import com.museum.museumsystem.dto.request.ArtifactQueryDTO;
import com.museum.museumsystem.entity.Acquisition;
import com.museum.museumsystem.entity.Appraisal;
import com.museum.museumsystem.entity.Artifact;
import com.museum.museumsystem.entity.ArtifactStateLog;
import com.museum.museumsystem.entity.Disposal;
import com.museum.museumsystem.entity.Exhibition;
import com.museum.museumsystem.entity.ExhibitionArtifact;
import com.museum.museumsystem.entity.Loan;
import com.museum.museumsystem.entity.Outbound;
import com.museum.museumsystem.entity.Restoration;
import com.museum.museumsystem.entity.StorageCheck;
import com.museum.museumsystem.entity.WarehouseIn;
import com.museum.museumsystem.entity.Warehousing;
import com.museum.museumsystem.mapper.AcquisitionMapper;
import com.museum.museumsystem.mapper.AppraisalMapper;
import com.museum.museumsystem.mapper.ArtifactMapper;
import com.museum.museumsystem.mapper.ArtifactStateLogMapper;
import com.museum.museumsystem.mapper.DisposalMapper;
import com.museum.museumsystem.mapper.ExhibitionArtifactMapper;
import com.museum.museumsystem.mapper.ExhibitionMapper;
import com.museum.museumsystem.mapper.LoanMapper;
import com.museum.museumsystem.mapper.OutboundMapper;
import com.museum.museumsystem.mapper.RestorationMapper;
import com.museum.museumsystem.mapper.StorageCheckMapper;
import com.museum.museumsystem.mapper.WarehouseInMapper;
import com.museum.museumsystem.mapper.WarehousingMapper;
import com.museum.museumsystem.service.ArtifactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtifactServiceImpl extends ServiceImpl<ArtifactMapper, Artifact> implements ArtifactService {

    private final ExhibitionArtifactMapper exhibitionArtifactMapper;
    private final ExhibitionMapper exhibitionMapper;
    private final RestorationMapper restorationMapper;
    private final LoanMapper loanMapper;
    private final AcquisitionMapper acquisitionMapper;
    private final AppraisalMapper appraisalMapper;
    private final OutboundMapper outboundMapper;
    private final DisposalMapper disposalMapper;
    private final StorageCheckMapper storageCheckMapper;
    private final WarehousingMapper warehousingMapper;
    private final WarehouseInMapper warehouseInMapper;
    private final ArtifactStateLogMapper artifactStateLogMapper;

    @Override
    public PageResult<Artifact> pageQuery(ArtifactQueryDTO queryDTO) {
        Page<Artifact> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());

        LambdaQueryWrapper<Artifact> wrapper = new LambdaQueryWrapper<>();
        applyArtifactFilters(wrapper, queryDTO.getCode(), queryDTO.getName(), queryDTO.getCategory(), queryDTO.getEra(),
                queryDTO.getMaterial(), queryDTO.getSource(), queryDTO.getPreservationStatus(), queryDTO.getAppraisalLevel(),
                queryDTO.getCurrentState(), queryDTO.getLocation());

        Page<Artifact> result = this.page(page, wrapper);
        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(), result.getPages(), result.getRecords());
    }

    @Override
    public Artifact getByCode(String code) {
        LambdaQueryWrapper<Artifact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Artifact::getCode, code);
        return this.getOne(wrapper);
    }

    @Override
    public boolean updateState(Long id, String targetState, String remark) {
        Artifact artifact = this.getById(id);
        if (artifact == null) {
            return false;
        }
        artifact.setCurrentState(targetState);
        return this.updateById(artifact);
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        Long artifactId = castId(id);
        Artifact artifact = getById(artifactId);
        if (artifact == null) {
            return false;
        }

        Map<String, Object> relations = getRelations(artifactId);
        List<String> relationLabels = new ArrayList<>();
        appendRelationLabel(relations, "exhibitions", "展览记录", relationLabels);
        appendRelationLabel(relations, "restorations", "修复记录", relationLabels);
        appendRelationLabel(relations, "loans", "外借记录", relationLabels);
        appendRelationLabel(relations, "acquisitions", "征集记录", relationLabels);
        appendRelationLabel(relations, "appraisals", "鉴定记录", relationLabels);
        appendRelationLabel(relations, "outbounds", "出库记录", relationLabels);
        appendRelationLabel(relations, "disposals", "处置记录", relationLabels);
        appendRelationLabel(relations, "storageChecks", "盘点记录", relationLabels);
        appendRelationLabel(relations, "warehousingRecords", "入库记录", relationLabels);
        appendRelationLabel(relations, "warehouseIns", "入馆记录", relationLabels);
        appendRelationLabel(relations, "stateLogs", "状态日志", relationLabels);
        if (!relationLabels.isEmpty()) {
            throw new BusinessException(400, "该文物存在关联业务数据（" + String.join("、", relationLabels) + "），不能直接删除");
        }

        exhibitionArtifactMapper.delete(new LambdaQueryWrapper<ExhibitionArtifact>()
                .eq(ExhibitionArtifact::getArtifactId, artifactId));
        return super.removeById(id);
    }

    @Override
    @Transactional
    public Map<String, Object> importArtifacts(ArtifactImportRequest request) {
        int successCount = 0;
        List<String> skippedCodes = new ArrayList<>();
        for (Artifact artifact : request.getArtifacts()) {
            if (!StringUtils.hasText(artifact.getCode()) || !StringUtils.hasText(artifact.getName())) {
                continue;
            }
            if (getByCode(artifact.getCode()) != null) {
                skippedCodes.add(artifact.getCode());
                continue;
            }
            save(artifact);
            successCount++;
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("importedCount", successCount);
        result.put("skippedCodes", skippedCodes);
        return result;
    }

    @Override
    public void exportArtifacts(ArtifactExportRequest request, HttpServletResponse response) throws IOException {
        List<Artifact> artifacts;
        if (request.getIds() != null && !request.getIds().isEmpty()) {
            artifacts = listByIds(request.getIds());
        } else {
            LambdaQueryWrapper<Artifact> wrapper = new LambdaQueryWrapper<>();
            String code = StringUtils.hasText(request.getCode()) ? request.getCode() : null;
            String name = StringUtils.hasText(request.getName()) ? request.getName() : request.getKeyword();
            applyArtifactFilters(wrapper, code, name, request.getCategory(), request.getEra(),
                    request.getMaterial(), request.getSource(), request.getPreservationStatus(), request.getAppraisalLevel(),
                    request.getCurrentState(), request.getLocation());
            artifacts = list(wrapper);
        }
        writeArtifactCsv("artifacts-export.csv", artifacts, response);
    }

    @Override
    public void exportArtifactById(Long id, HttpServletResponse response) throws IOException {
        Artifact artifact = getById(id);
        writeArtifactCsv("artifact-" + id + ".csv", artifact == null ? Collections.emptyList() : Collections.singletonList(artifact), response);
    }

    @Override
    public void exportArtifactLabel(Long id, HttpServletResponse response) throws IOException {
        Artifact artifact = getById(id);
        if (artifact == null) {
            throw new BusinessException(404, "鏂囩墿涓嶅瓨鍦?");
        }
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode("artifact-label-" + id + ".txt", "UTF-8"));
        StringBuilder builder = new StringBuilder();
        builder.append("鏂囩墿鏍囩").append('\n')
                .append("缂栧彿: ").append(safe(artifact.getCode())).append('\n')
                .append("鍚嶇О: ").append(safe(artifact.getName())).append('\n')
                .append("绫诲埆: ").append(safe(artifact.getCategory())).append('\n')
                .append("骞翠唬: ").append(safe(artifact.getEra())).append('\n')
                .append("浣嶇疆: ").append(safe(artifact.getLocation())).append('\n')
                .append("鐘舵€? ").append(safe(artifact.getCurrentState()));
        response.getWriter().write(builder.toString());
        response.getWriter().flush();
    }

    @Override
    public void exportArtifactReport(Long id, HttpServletResponse response) throws IOException {
        Artifact artifact = getById(id);
        if (artifact == null) {
            throw new RuntimeException("鏂囩墿涓嶅瓨鍦?");
        }
        Map<String, Object> relations = getRelations(id);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode("artifact-report-" + id + ".txt", "UTF-8"));
        StringBuilder builder = new StringBuilder();
        builder.append("鏂囩墿妗ｆ鎶ュ憡").append('\n')
                .append("缂栧彿: ").append(safe(artifact.getCode())).append('\n')
                .append("鍚嶇О: ").append(safe(artifact.getName())).append('\n')
                .append("绫诲埆: ").append(safe(artifact.getCategory())).append('\n')
                .append("淇濆瓨鐘舵€? ").append(safe(artifact.getPreservationStatus())).append('\n')
                .append("褰撳墠浣嶇疆: ").append(safe(artifact.getLocation())).append('\n')
                .append("灞曡璁板綍鏁? ").append(((List<?>) relations.get("exhibitions")).size()).append('\n')
                .append("淇璁板綍鏁? ").append(((List<?>) relations.get("restorations")).size()).append('\n')
                .append("澶栧€熻褰曟暟: ").append(((List<?>) relations.get("loans")).size()).append('\n');
        response.getWriter().write(builder.toString());
        response.getWriter().flush();
    }

    @Override
    @Transactional
    public void joinExhibition(Long artifactId, ArtifactJoinExhibitionRequest request) {
        Artifact artifact = getById(artifactId);
        if (artifact == null) {
            throw new BusinessException(404, "鏂囩墿涓嶅瓨鍦?");
        }
        Exhibition exhibition = exhibitionMapper.selectById(request.getExhibitionId());
        if (exhibition == null) {
            throw new BusinessException(404, "灞曡涓嶅瓨鍦?");
        }
        LambdaQueryWrapper<ExhibitionArtifact> wrapper = new LambdaQueryWrapper<ExhibitionArtifact>()
                .eq(ExhibitionArtifact::getExhibitionId, request.getExhibitionId())
                .eq(ExhibitionArtifact::getArtifactId, artifactId);
        if (exhibitionArtifactMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "璇ユ枃鐗╁凡鍦ㄥ綋鍓嶅睍瑙堜腑");
        }
        ExhibitionArtifact relation = new ExhibitionArtifact();
        relation.setExhibitionId(request.getExhibitionId());
        relation.setArtifactId(artifactId);
        relation.setDisplayOrder(request.getDisplayOrder());
        relation.setRemark(request.getRemark());
        exhibitionArtifactMapper.insert(relation);
        artifact.setCurrentState("IN_EXHIBITION");
        updateById(artifact);
    }

    @Override
    public Map<String, Object> getRelations(Long artifactId) {
        Artifact artifact = getById(artifactId);
        if (artifact == null) {
            throw new BusinessException(404, "鏂囩墿涓嶅瓨鍦?");
        }

        List<ExhibitionArtifact> exhibitionRelations = exhibitionArtifactMapper.selectList(new LambdaQueryWrapper<ExhibitionArtifact>()
                .eq(ExhibitionArtifact::getArtifactId, artifactId));
        List<Exhibition> exhibitions = exhibitionRelations.isEmpty()
                ? Collections.emptyList()
                : exhibitionMapper.selectBatchIds(exhibitionRelations.stream().map(ExhibitionArtifact::getExhibitionId).collect(Collectors.toList()));

        List<Restoration> restorations = restorationMapper.selectList(new LambdaQueryWrapper<Restoration>()
                .eq(Restoration::getArtifactId, artifactId)
                .or().eq(Restoration::getArtifactCode, artifact.getCode()));
        List<Loan> loans = loanMapper.selectList(new LambdaQueryWrapper<Loan>()
                .eq(Loan::getArtifactId, artifactId)
                .or().eq(Loan::getArtifactCode, artifact.getCode()));
        List<Acquisition> acquisitions = acquisitionMapper.selectList(new LambdaQueryWrapper<Acquisition>()
                .eq(Acquisition::getArtifactId, artifactId)
                .or().eq(Acquisition::getArtifactCode, artifact.getCode()));
        List<Appraisal> appraisals = appraisalMapper.selectList(new LambdaQueryWrapper<Appraisal>()
                .eq(Appraisal::getArtifactId, artifactId)
                .or().eq(Appraisal::getArtifactCode, artifact.getCode()));
        List<Outbound> outbounds = outboundMapper.selectList(new LambdaQueryWrapper<Outbound>()
                .eq(Outbound::getArtifactId, artifactId)
                .or().eq(Outbound::getArtifactCode, artifact.getCode()));
        List<Disposal> disposals = disposalMapper.selectList(new LambdaQueryWrapper<Disposal>()
                .eq(Disposal::getArtifactId, artifactId)
                .or().eq(Disposal::getArtifactCode, artifact.getCode()));
        List<StorageCheck> storageChecks = storageCheckMapper.selectList(new LambdaQueryWrapper<StorageCheck>()
                .eq(StorageCheck::getArtifactId, artifactId)
                .or().eq(StorageCheck::getArtifactCode, artifact.getCode()));
        List<Warehousing> warehousingRecords = warehousingMapper.selectList(new LambdaQueryWrapper<Warehousing>()
                .eq(Warehousing::getArtifactId, artifactId)
                .or().eq(Warehousing::getArtifactCode, artifact.getCode()));
        List<WarehouseIn> warehouseIns = warehouseInMapper.selectList(new LambdaQueryWrapper<WarehouseIn>()
                .eq(WarehouseIn::getArtifactId, artifactId));
        List<ArtifactStateLog> stateLogs = artifactStateLogMapper.selectList(new LambdaQueryWrapper<ArtifactStateLog>()
                .eq(ArtifactStateLog::getArtifactId, artifactId));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("exhibitions", exhibitions.stream().map(item -> relationItem(item.getId(), item.getName(), item.getStatus(),
                item.getStartDate(), item.getEndDate())).collect(Collectors.toList()));
        result.put("restorations", restorations.stream().map(item -> relationItem(item.getId(), item.getArtifactName(), item.getStatus(),
                item.getEstimatedStartDate(), item.getEstimatedEndDate())).collect(Collectors.toList()));
        result.put("loans", loans.stream().map(item -> relationItem(item.getId(), item.getArtifactName(), item.getStatus(),
                item.getLoanDate(), item.getExpectedReturnDate())).collect(Collectors.toList()));
        result.put("acquisitions", acquisitions.stream().map(item -> relationItem(item.getId(), item.getArtifactName(), item.getAcquisitionType(),
                item.getAcquisitionDate(), item.getCreatedTime())).collect(Collectors.toList()));
        result.put("appraisals", appraisals.stream().map(item -> relationItem(item.getId(), item.getArtifactName(), item.getAppraisalResult(),
                item.getAppraisalDate(), item.getUpdatedTime())).collect(Collectors.toList()));
        result.put("outbounds", outbounds.stream().map(item -> relationItem(item.getId(), item.getArtifactName(), item.getStatus(),
                item.getOutboundDate(), item.getActualReturnDate())).collect(Collectors.toList()));
        result.put("disposals", disposals.stream().map(item -> relationItem(item.getId(), item.getArtifactName(), item.getStatus(),
                item.getApplyDate(), item.getDisposalDate())).collect(Collectors.toList()));
        result.put("storageChecks", storageChecks.stream().map(item -> relationItem(item.getId(), item.getArtifactName(), item.getCheckResult(),
                item.getCheckDate(), item.getNextCheckDate())).collect(Collectors.toList()));
        result.put("warehousingRecords", warehousingRecords.stream().map(item -> relationItem(item.getId(), item.getArtifactName(), item.getStorageStatus(),
                item.getStorageDate(), item.getUpdatedTime())).collect(Collectors.toList()));
        result.put("warehouseIns", warehouseIns.stream().map(item -> relationItem(item.getId(), artifact.getName(), item.getLocation(),
                item.getInDate(), item.getCreatedTime())).collect(Collectors.toList()));
        result.put("stateLogs", stateLogs.stream().map(item -> relationItem(item.getId(), artifact.getName(), item.getToState(),
                item.getCreateTime(), item.getCreateTime())).collect(Collectors.toList()));
        return result;
    }

    private void applyArtifactFilters(LambdaQueryWrapper<Artifact> wrapper,
                                      String code,
                                      String name,
                                      String category,
                                      String era,
                                      String material,
                                      String source,
                                      String preservationStatus,
                                      String appraisalLevel,
                                      String currentState,
                                      String location) {
        if (StringUtils.hasText(code)) {
            wrapper.eq(Artifact::getCode, code);
        }
        if (StringUtils.hasText(name)) {
            wrapper.like(Artifact::getName, name);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(Artifact::getCategory, category);
        }
        if (StringUtils.hasText(era)) {
            wrapper.eq(Artifact::getEra, era);
        }
        if (StringUtils.hasText(material)) {
            wrapper.eq(Artifact::getMaterial, material);
        }
        if (StringUtils.hasText(source)) {
            wrapper.eq(Artifact::getSource, source);
        }
        if (StringUtils.hasText(preservationStatus)) {
            wrapper.eq(Artifact::getPreservationStatus, preservationStatus);
        }
        if (StringUtils.hasText(appraisalLevel)) {
            wrapper.eq(Artifact::getAppraisalLevel, appraisalLevel);
        }
        if (StringUtils.hasText(currentState)) {
            wrapper.eq(Artifact::getCurrentState, currentState);
        }
        if (StringUtils.hasText(location)) {
            wrapper.eq(Artifact::getLocation, location);
        }
    }

    private void writeArtifactCsv(String fileName, List<Artifact> artifacts, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));
        StringBuilder csv = new StringBuilder();
        csv.append('\ufeff');
        csv.append("ID,编号,名称,类别,年代,材质,保存状态,鉴定等级,当前状态,位置\n");
        for (Artifact artifact : artifacts) {
            csv.append(csvValue(artifact.getId())).append(',')
                    .append(csvValue(artifact.getCode())).append(',')
                    .append(csvValue(artifact.getName())).append(',')
                    .append(csvValue(artifact.getCategory())).append(',')
                    .append(csvValue(artifact.getEra())).append(',')
                    .append(csvValue(artifact.getMaterial())).append(',')
                    .append(csvValue(artifact.getPreservationStatus())).append(',')
                    .append(csvValue(artifact.getAppraisalLevel())).append(',')
                    .append(csvValue(artifact.getCurrentState())).append(',')
                    .append(csvValue(artifact.getLocation()))
                    .append('\n');
        }
        response.getWriter().write(csv.toString());
        response.getWriter().flush();
    }

    private Map<String, Object> relationItem(Long id, String name, String status, Object startTime, Object endTime) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", id);
        item.put("name", name);
        item.put("title", name);
        item.put("status", status);
        item.put("startTime", startTime);
        item.put("endTime", endTime);
        return item;
    }

    private void appendRelationLabel(Map<String, Object> relations, String key, String label, List<String> collector) {
        List<?> items = (List<?>) relations.getOrDefault(key, Collections.emptyList());
        if (!items.isEmpty()) {
            collector.add(label);
        }
    }

    private Long castId(Serializable id) {
        if (id instanceof Long) {
            return (Long) id;
        }
        if (id instanceof Number) {
            return ((Number) id).longValue();
        }
        return Long.valueOf(String.valueOf(id));
    }

    private String csvValue(Object value) {
        String text = value == null ? "" : String.valueOf(value);
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }

    private String safe(String value) {
        return value == null ? "-" : value;
    }
}
