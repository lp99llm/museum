package com.museum.museumsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.museum.museumsystem.dto.request.ArtifactExportRequest;
import com.museum.museumsystem.dto.request.ArtifactImportRequest;
import com.museum.museumsystem.dto.request.ArtifactJoinExhibitionRequest;
import com.museum.museumsystem.entity.Artifact;
import com.museum.museumsystem.dto.request.ArtifactQueryDTO;
import com.museum.museumsystem.common.PageResult;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ArtifactService extends IService<Artifact> {
    PageResult<Artifact> pageQuery(ArtifactQueryDTO queryDTO);
    Artifact getByCode(String code);
    boolean updateState(Long id, String targetState, String remark);
    Map<String, Object> importArtifacts(ArtifactImportRequest request);
    void exportArtifacts(ArtifactExportRequest request, HttpServletResponse response) throws IOException;
    void exportArtifactById(Long id, HttpServletResponse response) throws IOException;
    void exportArtifactLabel(Long id, HttpServletResponse response) throws IOException;
    void exportArtifactReport(Long id, HttpServletResponse response) throws IOException;
    void joinExhibition(Long artifactId, ArtifactJoinExhibitionRequest request);
    Map<String, Object> getRelations(Long artifactId);
}
