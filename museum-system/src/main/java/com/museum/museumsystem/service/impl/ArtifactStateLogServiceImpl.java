package com.museum.museumsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.museum.museumsystem.entity.ArtifactStateLog;
import com.museum.museumsystem.mapper.ArtifactStateLogMapper;
import com.museum.museumsystem.service.ArtifactStateLogService;
import org.springframework.stereotype.Service;

@Service
public class ArtifactStateLogServiceImpl extends ServiceImpl<ArtifactStateLogMapper, ArtifactStateLog> implements ArtifactStateLogService {
}