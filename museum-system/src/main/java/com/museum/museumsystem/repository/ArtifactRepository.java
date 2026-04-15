package com.museum.museumsystem.repository;

import com.museum.museumsystem.entity.Artifact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtifactRepository extends JpaRepository<Artifact, Long> {
    @Query("SELECT a FROM Artifact a WHERE " +
            "(:keyword IS NULL OR a.name LIKE %:keyword% OR a.code LIKE %:keyword%) AND " +
            "(:category IS NULL OR a.category = :category) AND " +
            "(:state IS NULL OR a.currentState = :state)")
    Page<Artifact> search(@Param("keyword") String keyword,
                          @Param("category") String category,
                          @Param("state") Artifact.ArtifactState state,
                          Pageable pageable);
}