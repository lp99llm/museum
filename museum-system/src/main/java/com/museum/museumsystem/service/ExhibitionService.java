package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.dto.request.ExhibitionArtifactDTO;
import com.museum.museumsystem.dto.request.ExhibitionMaterialUploadRequest;
import com.museum.museumsystem.dto.request.ExhibitionQueryDTO;
import com.museum.museumsystem.entity.Exhibition;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ExhibitionService extends IService<Exhibition> {
    PageResult<Exhibition> pageQuery(ExhibitionQueryDTO queryDTO);
    boolean updateExhibition(Exhibition exhibition);

    boolean addArtifactToExhibition(Long exhibitionId, Long artifactId, Integer displayOrder, String remark);

    boolean removeArtifactFromExhibition(Long exhibitionId, Long artifactId);

    List<Long> getArtifactIdsByExhibitionId(Long exhibitionId);

    List<ExhibitionArtifactDTO> getArtifactDetailsByExhibitionId(Long exhibitionId);

    void exportArtifacts(Long exhibitionId, HttpServletResponse response) throws IOException;

    void exportEvaluationReport(Long exhibitionId, HttpServletResponse response) throws IOException;

    List<Map<String, Object>> uploadMaterial(Long exhibitionId, ExhibitionMaterialUploadRequest request);

    List<Map<String, Object>> uploadMaterialFile(Long exhibitionId, String name, String type, MultipartFile file) throws IOException;

    List<Map<String, Object>> getMaterials(Long exhibitionId);

    void deleteMaterial(Long exhibitionId, Long materialId);

    Map<String, Object> getVisitorStats(Long exhibitionId);

    void downloadMaterial(Long exhibitionId, Long materialId, HttpServletResponse response) throws IOException;
}
