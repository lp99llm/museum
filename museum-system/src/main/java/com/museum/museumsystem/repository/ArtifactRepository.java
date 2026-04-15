package com.museum.museumsystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.museum.museumsystem.entity.Artifact;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtifactRepository extends BaseMapper<Artifact> {

    Page<Artifact> search(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("state") String state,
            Page<Artifact> page
    );
}