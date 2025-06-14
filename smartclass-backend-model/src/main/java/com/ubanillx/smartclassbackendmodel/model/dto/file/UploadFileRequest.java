package com.ubanillx.smartclassbackendmodel.model.dto.file;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传请求
*/
@Data
public class UploadFileRequest implements Serializable {

    /**
     * 文件名称（可选）
     */
    private String filename;

    /**
     * 文件描述（可选）
     */
    private String description;

    private static final long serialVersionUID = 1L;
}