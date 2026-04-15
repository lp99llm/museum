package com.museum.museumsystem.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "artifact")
public class Artifact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;          // 文物编号
    private String name;          // 名称
    private String category;      // 类别
    private String era;           // 年代
    private String location;      // 出土地点/存放位置
    private String currentLocation;
    private String description;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private ArtifactState currentState = ArtifactState.IN_STORAGE;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    public enum ArtifactState {
        ACQUIRED, IN_STORAGE, IN_EXHIBITION, IN_RESTORATION, ON_LOAN, DISPOSED
    }
}