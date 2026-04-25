package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.museum.museumsystem.common.BusinessException;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.ExhibitionArtifactDTO;
import com.museum.museumsystem.dto.request.ExhibitionMaterialUploadRequest;
import com.museum.museumsystem.dto.request.ExhibitionQueryDTO;
import com.museum.museumsystem.entity.Artifact;
import com.museum.museumsystem.entity.Exhibition;
import com.museum.museumsystem.entity.ExhibitionArtifact;
import com.museum.museumsystem.entity.ExhibitionEvaluation;
import com.museum.museumsystem.entity.VisitorAppointment;
import com.museum.museumsystem.mapper.ArtifactMapper;
import com.museum.museumsystem.mapper.ExhibitionEvaluationMapper;
import com.museum.museumsystem.mapper.ExhibitionMapper;
import com.museum.museumsystem.mapper.VisitorAppointmentMapper;
import com.museum.museumsystem.service.ExhibitionArtifactService;
import com.museum.museumsystem.service.ExhibitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ExhibitionServiceImpl extends ServiceImpl<ExhibitionMapper, Exhibition> implements ExhibitionService {

    private final ExhibitionArtifactService exhibitionArtifactService;
    private final ArtifactMapper artifactMapper;
    private final ExhibitionEvaluationMapper exhibitionEvaluationMapper;
    private final VisitorAppointmentMapper visitorAppointmentMapper;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public PageResult<Exhibition> pageQuery(ExhibitionQueryDTO queryDTO) {
        Page<Exhibition> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        LambdaQueryWrapper<Exhibition> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getName()), Exhibition::getName, queryDTO.getName())
                .eq(StringUtils.hasText(queryDTO.getStatus()), Exhibition::getStatus, queryDTO.getStatus())
                .ge(StringUtils.hasText(queryDTO.getStartDate()), Exhibition::getStartDate, queryDTO.getStartDate())
                .le(StringUtils.hasText(queryDTO.getEndDate()), Exhibition::getStartDate, queryDTO.getEndDate())
                .orderByDesc(Exhibition::getCreatedTime);

        Page<Exhibition> pageResult = this.page(page, wrapper);
        PageResult<Exhibition> result = new PageResult<>();
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setPages(pageResult.getPages());
        result.setRecords(pageResult.getRecords());
        return result;
    }

    @Override
    @Transactional
    public boolean updateExhibition(Exhibition exhibition) {
        Exhibition existing = getById(exhibition.getId());
        if (existing == null) {
            throw new BusinessException(404, "展览不存在");
        }

        boolean updated = updateById(exhibition);
        if (updated && !"FINISHED".equals(existing.getStatus()) && "FINISHED".equals(exhibition.getStatus())) {
            syncArtifactsAfterExhibitionFinished(exhibition.getId());
        }
        return updated;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        Long exhibitionId = castId(id);
        Exhibition exhibition = getById(exhibitionId);
        if (exhibition == null) {
            return false;
        }

        List<Long> artifactIds = getArtifactIdsByExhibitionId(exhibitionId);
        exhibitionArtifactService.remove(new LambdaQueryWrapper<ExhibitionArtifact>()
                .eq(ExhibitionArtifact::getExhibitionId, exhibitionId));

        deleteMaterialsDirectory(exhibitionId);
        boolean removed = super.removeById(id);
        if (removed) {
            artifactIds.stream()
                    .filter(Objects::nonNull)
                    .forEach(artifactId -> syncArtifactStateAfterRelationChange(artifactId, exhibitionId));
        }
        return removed;
    }

    @Override
    @Transactional
    public boolean addArtifactToExhibition(Long exhibitionId, Long artifactId, Integer displayOrder, String remark) {
        Exhibition exhibition = getById(exhibitionId);
        if (exhibition == null) {
            throw new BusinessException(404, "展览不存在");
        }
        Artifact artifact = artifactMapper.selectById(artifactId);
        if (artifact == null) {
            throw new BusinessException(404, "文物不存在");
        }

        LambdaQueryWrapper<ExhibitionArtifact> duplicateWrapper = new LambdaQueryWrapper<ExhibitionArtifact>()
                .eq(ExhibitionArtifact::getExhibitionId, exhibitionId)
                .eq(ExhibitionArtifact::getArtifactId, artifactId);
        if (exhibitionArtifactService.count(duplicateWrapper) > 0) {
            throw new BusinessException(400, "该文物已加入当前展览，请勿重复添加");
        }

        List<ExhibitionArtifact> existingRelations = exhibitionArtifactService.list(
                new LambdaQueryWrapper<ExhibitionArtifact>().eq(ExhibitionArtifact::getArtifactId, artifactId)
        );
        if (!existingRelations.isEmpty()) {
            List<Long> relatedExhibitionIds = existingRelations.stream()
                    .map(ExhibitionArtifact::getExhibitionId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (!relatedExhibitionIds.isEmpty()) {
                boolean inAnotherActiveExhibition = this.listByIds(relatedExhibitionIds).stream()
                        .filter(Objects::nonNull)
                        .anyMatch(item -> !"FINISHED".equals(item.getStatus()));
                if (inAnotherActiveExhibition) {
                    throw new BusinessException(400, "该文物已在其他未结束展览中，无法重复加入");
                }
            }
        }

        ExhibitionArtifact relation = new ExhibitionArtifact();
        relation.setExhibitionId(exhibitionId);
        relation.setArtifactId(artifactId);
        relation.setDisplayOrder(displayOrder);
        relation.setRemark(remark);
        boolean saved = exhibitionArtifactService.save(relation);
        if (saved) {
            artifact.setCurrentState("IN_EXHIBITION");
            artifactMapper.updateById(artifact);
        }
        return saved;
    }

    @Override
    @Transactional
    public boolean removeArtifactFromExhibition(Long exhibitionId, Long artifactId) {
        Artifact artifact = artifactMapper.selectById(artifactId);
        LambdaQueryWrapper<ExhibitionArtifact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExhibitionArtifact::getExhibitionId, exhibitionId)
                .eq(ExhibitionArtifact::getArtifactId, artifactId);
        boolean removed = exhibitionArtifactService.remove(wrapper);
        if (removed && artifact != null) {
            syncArtifactStateAfterRelationChange(artifactId, exhibitionId);
        }
        return removed;
    }

    @Override
    public List<Long> getArtifactIdsByExhibitionId(Long exhibitionId) {
        LambdaQueryWrapper<ExhibitionArtifact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExhibitionArtifact::getExhibitionId, exhibitionId);
        return exhibitionArtifactService.list(wrapper).stream()
                .map(ExhibitionArtifact::getArtifactId)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExhibitionArtifactDTO> getArtifactDetailsByExhibitionId(Long exhibitionId) {
        LambdaQueryWrapper<ExhibitionArtifact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExhibitionArtifact::getExhibitionId, exhibitionId)
                .orderByAsc(ExhibitionArtifact::getDisplayOrder);
        List<ExhibitionArtifact> relations = exhibitionArtifactService.list(wrapper);

        if (relations.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> artifactIds = relations.stream()
                .map(ExhibitionArtifact::getArtifactId)
                .collect(Collectors.toList());
        Map<Long, Artifact> artifactMap = artifactMapper.selectBatchIds(artifactIds).stream()
                .collect(Collectors.toMap(Artifact::getId, artifact -> artifact));

        return relations.stream().map(rel -> {
            ExhibitionArtifactDTO dto = new ExhibitionArtifactDTO();
            dto.setArtifactId(rel.getArtifactId());
            dto.setDisplayOrder(rel.getDisplayOrder());
            dto.setRemark(rel.getRemark());

            Artifact artifact = artifactMap.get(rel.getArtifactId());
            if (artifact != null) {
                dto.setCode(artifact.getCode());
                dto.setName(artifact.getName());
                dto.setCategory(artifact.getCategory());
                dto.setEra(artifact.getEra());
                dto.setMaterial(artifact.getMaterial());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void exportArtifacts(Long exhibitionId, HttpServletResponse response) throws IOException {
        List<ExhibitionArtifactDTO> records = getArtifactDetailsByExhibitionId(exhibitionId);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename*=UTF-8''" + URLEncoder.encode("exhibition-artifacts-" + exhibitionId + ".csv", "UTF-8")
        );

        StringBuilder csv = new StringBuilder();
        csv.append('\ufeff');
        csv.append("文物ID,编号,名称,类别,年代,材质,展陈顺序,备注\n");
        for (ExhibitionArtifactDTO item : records) {
            csv.append(csvValue(item.getArtifactId())).append(',')
                    .append(csvValue(item.getCode())).append(',')
                    .append(csvValue(item.getName())).append(',')
                    .append(csvValue(item.getCategory())).append(',')
                    .append(csvValue(item.getEra())).append(',')
                    .append(csvValue(item.getMaterial())).append(',')
                    .append(csvValue(item.getDisplayOrder())).append(',')
                    .append(csvValue(item.getRemark()))
                    .append('\n');
        }
        response.getWriter().write(csv.toString());
        response.getWriter().flush();
    }

    @Override
    public void exportEvaluationReport(Long exhibitionId, HttpServletResponse response) throws IOException {
        Exhibition exhibition = getById(exhibitionId);
        ExhibitionEvaluation evaluation = exhibitionEvaluationMapper.selectOne(
                new LambdaQueryWrapper<ExhibitionEvaluation>()
                        .eq(ExhibitionEvaluation::getExhibitionId, exhibitionId)
                        .orderByDesc(ExhibitionEvaluation::getEvaluationDate)
                        .last("limit 1")
        );
        Map<String, Object> visitorStats = getVisitorStats(exhibitionId);

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename*=UTF-8''" + URLEncoder.encode("exhibition-evaluation-" + exhibitionId + ".txt", "UTF-8")
        );

        StringBuilder builder = new StringBuilder();
        builder.append("展览评估报告\n")
                .append("展览名称: ").append(exhibition == null ? "-" : safe(exhibition.getName())).append('\n')
                .append("主题: ").append(exhibition == null ? "-" : safe(exhibition.getTheme())).append('\n')
                .append("预约人数: ").append(safe(visitorStats.get("reservedCount"))).append('\n')
                .append("实际参观人数: ").append(safe(visitorStats.get("visitedCount"))).append('\n')
                .append("满场率: ").append(safe(visitorStats.get("occupancyRate"))).append('\n');

        if (evaluation != null) {
            builder.append("反馈评分: ").append(safe(evaluation.getFeedbackScore())).append('\n')
                    .append("安全事件: ").append(safe(evaluation.getSafetyIncidents())).append('\n')
                    .append("媒体传播: ").append(safe(evaluation.getMediaCoverage())).append('\n')
                    .append("亮点: ").append(safe(evaluation.getMajorHighlights())).append('\n')
                    .append("问题: ").append(safe(evaluation.getProblems())).append('\n')
                    .append("改进建议: ").append(safe(evaluation.getImprovementSuggestions())).append('\n');
        }

        response.getWriter().write(builder.toString());
        response.getWriter().flush();
    }

    @Override
    public List<Map<String, Object>> uploadMaterial(Long exhibitionId, ExhibitionMaterialUploadRequest request) {
        ensureExhibitionExists(exhibitionId);
        List<Map<String, Object>> materials = getMaterials(exhibitionId);
        long materialId = System.currentTimeMillis();

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", materialId);
        item.put("name", request.getName());
        item.put("type", request.getType());
        item.put("url", "/api/exhibition/" + exhibitionId + "/materials/" + materialId + "/download");
        item.put("uploadedAt", LocalDateTime.now());
        item.put("storageType", "LINK");
        item.put("sourceUrl", request.getUrl());
        materials.add(item);

        saveMaterials(exhibitionId, materials);
        return materials;
    }

    @Override
    public List<Map<String, Object>> uploadMaterialFile(Long exhibitionId, String name, String type, MultipartFile file) throws IOException {
        ensureExhibitionExists(exhibitionId);
        if (!StringUtils.hasText(name) || !StringUtils.hasText(type)) {
            throw new BusinessException(400, "资料名称和类型不能为空");
        }
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "请先选择上传文件");
        }

        long materialId = System.currentTimeMillis();
        String originalFileName = StringUtils.hasText(file.getOriginalFilename()) ? file.getOriginalFilename() : "material.bin";
        String storedFileName = materialId + "-" + sanitizeFilename(originalFileName);
        Path directory = materialsFileDir(exhibitionId);
        Files.createDirectories(directory);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, directory.resolve(storedFileName), StandardCopyOption.REPLACE_EXISTING);
        }

        List<Map<String, Object>> materials = getMaterials(exhibitionId);
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", materialId);
        item.put("name", name);
        item.put("type", type);
        item.put("url", "/api/exhibition/" + exhibitionId + "/materials/" + materialId + "/download");
        item.put("uploadedAt", LocalDateTime.now());
        item.put("storageType", "FILE");
        item.put("fileName", storedFileName);
        item.put("originalFileName", originalFileName);
        item.put("contentType", file.getContentType());
        item.put("fileSize", file.getSize());
        materials.add(item);

        saveMaterials(exhibitionId, materials);
        return materials;
    }

    @Override
    public List<Map<String, Object>> getMaterials(Long exhibitionId) {
        Path path = materialsPath(exhibitionId);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(path.toFile(), new TypeReference<List<Map<String, Object>>>() {});
        } catch (IOException ex) {
            throw new BusinessException(500, "读取展览资料失败");
        }
    }

    @Override
    public void deleteMaterial(Long exhibitionId, Long materialId) {
        List<Map<String, Object>> materials = getMaterials(exhibitionId);
        Map<String, Object> target = materials.stream()
                .filter(item -> String.valueOf(item.get("id")).equals(String.valueOf(materialId)))
                .findFirst()
                .orElse(null);

        if (target != null && "FILE".equals(String.valueOf(target.get("storageType")))) {
            Object storedFileName = target.get("fileName");
            if (storedFileName != null) {
                try {
                    Files.deleteIfExists(materialsFileDir(exhibitionId).resolve(String.valueOf(storedFileName)));
                } catch (IOException ignored) {
                }
            }
        }

        List<Map<String, Object>> remaining = materials.stream()
                .filter(item -> !String.valueOf(item.get("id")).equals(String.valueOf(materialId)))
                .collect(Collectors.toList());
        saveMaterials(exhibitionId, remaining);
    }

    @Override
    public Map<String, Object> getVisitorStats(Long exhibitionId) {
        List<VisitorAppointment> appointments = visitorAppointmentMapper.selectList(
                new LambdaQueryWrapper<VisitorAppointment>()
                        .eq(VisitorAppointment::getExhibitionId, exhibitionId)
                        .orderByAsc(VisitorAppointment::getAppointmentDate)
        );

        int reservedCount = appointments.stream()
                .filter(item -> !"CANCELLED".equals(item.getStatus()) && !"REJECTED".equals(item.getStatus()))
                .mapToInt(item -> item.getVisitorCount() == null ? 0 : item.getVisitorCount())
                .sum();

        int visitedCount = appointments.stream()
                .filter(item -> "APPROVED".equals(item.getStatus()))
                .mapToInt(item -> item.getVisitorCount() == null ? 0 : item.getVisitorCount())
                .sum();

        double occupancyRate = reservedCount == 0 ? 0D : (visitedCount * 100D / reservedCount);

        Map<LocalDate, Integer> reservedTrend = new LinkedHashMap<>();
        Map<LocalDate, Integer> visitedTrend = new LinkedHashMap<>();
        for (VisitorAppointment appointment : appointments) {
            LocalDate date = appointment.getAppointmentDate();
            int count = appointment.getVisitorCount() == null ? 0 : appointment.getVisitorCount();
            if (!"CANCELLED".equals(appointment.getStatus()) && !"REJECTED".equals(appointment.getStatus())) {
                reservedTrend.put(date, reservedTrend.getOrDefault(date, 0) + count);
            }
            if ("APPROVED".equals(appointment.getStatus())) {
                visitedTrend.put(date, visitedTrend.getOrDefault(date, 0) + count);
            }
        }

        List<Map<String, Object>> trendData = reservedTrend.keySet().stream()
                .sorted()
                .map(date -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("date", date);
                    item.put("reservedCount", reservedTrend.getOrDefault(date, 0));
                    item.put("visitedCount", visitedTrend.getOrDefault(date, 0));
                    item.put("occupancyRate", reservedTrend.getOrDefault(date, 0) == 0
                            ? 0
                            : Math.round(visitedTrend.getOrDefault(date, 0) * 10000.0 / reservedTrend.getOrDefault(date, 0)) / 100.0);
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("reservedCount", reservedCount);
        result.put("visitedCount", visitedCount);
        result.put("occupancyRate", String.format("%.2f%%", occupancyRate));
        result.put("trendData", trendData);
        return result;
    }

    @Override
    public void downloadMaterial(Long exhibitionId, Long materialId, HttpServletResponse response) throws IOException {
        Map<String, Object> material = getMaterials(exhibitionId).stream()
                .filter(item -> String.valueOf(item.get("id")).equals(String.valueOf(materialId)))
                .findFirst()
                .orElseThrow(() -> new BusinessException(404, "资料不存在"));

        if ("FILE".equals(String.valueOf(material.get("storageType")))) {
            Path filePath = materialsFileDir(exhibitionId).resolve(String.valueOf(material.get("fileName")));
            if (!Files.exists(filePath)) {
                throw new BusinessException(404, "资料文件不存在");
            }
            Object contentType = material.get("contentType");
            String resolvedContentType = contentType == null ? "" : String.valueOf(contentType);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(
                    StringUtils.hasText(resolvedContentType)
                            ? resolvedContentType
                            : "application/octet-stream"
            );
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename*=UTF-8''" + URLEncoder.encode(String.valueOf(material.get("originalFileName")), "UTF-8")
            );
            Files.copy(filePath, response.getOutputStream());
            response.getOutputStream().flush();
            return;
        }

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename*=UTF-8''" + URLEncoder.encode(String.valueOf(material.get("name")) + ".txt", "UTF-8")
        );
        response.getWriter().write("name: " + safe(material.get("name")) + "\n");
        response.getWriter().write("type: " + safe(material.get("type")) + "\n");
        response.getWriter().write("sourceUrl: " + safe(material.get("sourceUrl")) + "\n");
        response.getWriter().flush();
    }

    private void ensureExhibitionExists(Long exhibitionId) {
        if (getById(exhibitionId) == null) {
            throw new BusinessException(404, "展览不存在");
        }
    }

    private void syncArtifactsAfterExhibitionFinished(Long exhibitionId) {
        List<Long> artifactIds = exhibitionArtifactService.list(
                new LambdaQueryWrapper<ExhibitionArtifact>().eq(ExhibitionArtifact::getExhibitionId, exhibitionId)
        ).stream()
                .map(ExhibitionArtifact::getArtifactId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        for (Long artifactId : artifactIds) {
            syncArtifactStateAfterRelationChange(artifactId, exhibitionId);
        }
    }

    private void syncArtifactStateAfterRelationChange(Long artifactId, Long excludeExhibitionId) {
        Artifact artifact = artifactMapper.selectById(artifactId);
        if (artifact == null) {
            return;
        }
        if (!hasOtherActiveExhibition(artifactId, excludeExhibitionId) && "IN_EXHIBITION".equals(artifact.getCurrentState())) {
            artifact.setCurrentState("IN_STORAGE");
            artifactMapper.updateById(artifact);
        }
    }

    private boolean hasOtherActiveExhibition(Long artifactId, Long excludeExhibitionId) {
        return exhibitionArtifactService.list(
                new LambdaQueryWrapper<ExhibitionArtifact>().eq(ExhibitionArtifact::getArtifactId, artifactId)
        ).stream()
                .map(ExhibitionArtifact::getExhibitionId)
                .filter(Objects::nonNull)
                .filter(id -> excludeExhibitionId == null || !excludeExhibitionId.equals(id))
                .map(this::getById)
                .filter(Objects::nonNull)
                .anyMatch(item -> !"FINISHED".equals(item.getStatus()));
    }

    private void deleteMaterialsDirectory(Long exhibitionId) {
        Path baseDir = Paths.get("data", "exhibition-materials", String.valueOf(exhibitionId));
        if (!Files.exists(baseDir)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(baseDir)) {
            walk.sorted((a, b) -> b.getNameCount() - a.getNameCount())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }
                    });
        } catch (IOException ignored) {
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

    private Path materialsPath(Long exhibitionId) {
        return Paths.get("data", "exhibition-materials", String.valueOf(exhibitionId), "materials.json");
    }

    private Path materialsFileDir(Long exhibitionId) {
        return Paths.get("data", "exhibition-materials", String.valueOf(exhibitionId), "files");
    }

    private void saveMaterials(Long exhibitionId, List<Map<String, Object>> materials) {
        Path path = materialsPath(exhibitionId);
        try {
            Files.createDirectories(path.getParent());
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), materials);
        } catch (IOException ex) {
            throw new BusinessException(500, "保存展览资料失败");
        }
    }

    private String csvValue(Object value) {
        String text = value == null ? "" : String.valueOf(value);
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }

    private String sanitizeFilename(String fileName) {
        return fileName.replaceAll("[\\\\/:*?\"<>|]+", "_");
    }

    private String safe(Object value) {
        return value == null ? "-" : String.valueOf(value);
    }
}
