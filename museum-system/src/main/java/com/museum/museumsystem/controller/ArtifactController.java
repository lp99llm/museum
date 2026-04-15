package com.museum.museumsystem.controller;

import com.museum.museumsystem.entity.Artifact;
import com.museum.museumsystem.service.ArtifactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/artifacts")
public class ArtifactController {
    @Autowired
    private ArtifactService artifactService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String currentState,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<Artifact> pageResult = artifactService.getList(keyword, category, currentState, page, pageSize);
        Map<String, Object> response = new HashMap<>();
        response.put("records", pageResult.getContent());
        response.put("total", pageResult.getTotalElements());
        response.put("current", pageResult.getNumber() + 1);
        response.put("size", pageResult.getSize());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Artifact> getById(@PathVariable Long id) {
        return ResponseEntity.ok(artifactService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Artifact> create(@RequestBody Artifact artifact) {
        return ResponseEntity.ok(artifactService.create(artifact));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Artifact> update(@PathVariable Long id, @RequestBody Artifact artifact) {
        return ResponseEntity.ok(artifactService.update(id, artifact));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        artifactService.delete(id);
        return ResponseEntity.ok("删除成功");
    }
}