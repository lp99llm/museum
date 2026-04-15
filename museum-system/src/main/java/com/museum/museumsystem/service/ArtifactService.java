package com.museum.museumsystem.service;

import com.museum.museumsystem.entity.Artifact;
import com.museum.museumsystem.repository.ArtifactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ArtifactService {
    @Autowired
    private ArtifactRepository artifactRepository;

    public Page<Artifact> getList(String keyword, String category, String state, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Artifact.ArtifactState stateEnum = null;
        if (state != null && !state.isEmpty()) {
            try {
                stateEnum = Artifact.ArtifactState.valueOf(state);
            } catch (IllegalArgumentException ignored) {}
        }
        return artifactRepository.search(keyword, category, stateEnum, pageable);
    }

    public Artifact getById(Long id) {
        return artifactRepository.findById(id).orElseThrow(() -> new RuntimeException("文物不存在"));
    }

    public Artifact create(Artifact artifact) {
        return artifactRepository.save(artifact);
    }

    public Artifact update(Long id, Artifact artifactDetails) {
        Artifact artifact = getById(id);
        artifact.setCode(artifactDetails.getCode());
        artifact.setName(artifactDetails.getName());
        artifact.setCategory(artifactDetails.getCategory());
        artifact.setEra(artifactDetails.getEra());
        artifact.setLocation(artifactDetails.getLocation());
        artifact.setCurrentLocation(artifactDetails.getCurrentLocation());
        artifact.setDescription(artifactDetails.getDescription());
        artifact.setImageUrl(artifactDetails.getImageUrl());
        artifact.setCurrentState(artifactDetails.getCurrentState());
        return artifactRepository.save(artifact);
    }

    public void delete(Long id) {
        artifactRepository.deleteById(id);
    }
}