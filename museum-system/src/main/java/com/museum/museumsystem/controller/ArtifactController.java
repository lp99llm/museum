package com.museum.museumsystem.controller;

import com.museum.museumsystem.service.ArtifactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/artifact")
public class ArtifactController {

    // 正确
    @Autowired
    private ArtifactService artifactService;

}