package com.museum.museumsystem.controller;

import com.museum.museumsystem.service.ArtifactService;
import com.museum.museumsystem.dto.request.ArtifactQueryDTO;
import com.museum.museumsystem.common.PageResult;
import com.museum.museumsystem.entity.Artifact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/artifact")
public class ArtifactController {

    @Autowired
    private ArtifactService artifactService;

    @PostMapping("/page")
    public PageResult<Artifact> pageQuery(@RequestBody ArtifactQueryDTO queryDTO) {
        return artifactService.pageQuery(queryDTO);
    }

    @GetMapping("/code/{code}")
    public Artifact getByCode(@PathVariable String code) {
        return artifactService.getByCode(code);
    }

    @PutMapping("/state/{id}")
    public boolean updateState(@PathVariable Long id, @RequestParam String targetState, @RequestParam String remark) {
        return artifactService.updateState(id, targetState, remark);
    }

    @PostMapping
    public boolean save(@RequestBody Artifact artifact) {
        return artifactService.save(artifact);
    }

    @PutMapping
    public boolean update(@RequestBody Artifact artifact) {
        return artifactService.updateById(artifact);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return artifactService.removeById(id);
    }

    @GetMapping("/{id}")
    public Artifact getById(@PathVariable Long id) {
        return artifactService.getById(id);
    }

}