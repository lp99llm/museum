package com.museum.museumsystem.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ExhibitionMaterialUploadRequest {
    @NotBlank(message = "资料名称不能为空")
    private String name;
    @NotBlank(message = "资料类型不能为空")
    private String type;
    @NotBlank(message = "资料链接不能为空")
    private String url;
}
