package com.museum.museumsystem.dto.request;

import com.museum.museumsystem.entity.Artifact;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class ArtifactImportRequest {

    @Valid
    @NotEmpty(message = "导入文物列表不能为空")
    private List<Artifact> artifacts;
}
