package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.entity.ExhibitionArtifact;
import com.museum.museumsystem.mapper.ExhibitionArtifactMapper;
import com.museum.museumsystem.service.ExhibitionArtifactService;
import org.springframework.stereotype.Service;

@Service
public class ExhibitionArtifactServiceImpl extends ServiceImpl<ExhibitionArtifactMapper, ExhibitionArtifact> implements ExhibitionArtifactService {
}