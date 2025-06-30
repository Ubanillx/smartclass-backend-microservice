package com.ubanillx.smartclassbackendserviceclient.service;

import com.ubanillx.smartclassbackendcommon.common.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;



/**
 * 文件服务客户端
 */
@FeignClient(name = "smartclass-file-service", path = "/api/file/inner")
public interface FileFeignClient {

    /**
     * 通用文件上传
     *
     * @param file 上传的文件
     * @return 文件访问URL
     */
    @PostMapping("/upload")
    BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile file);

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return 头像URL
     */
    @PostMapping("/upload/avatar")
    BaseResponse<String> uploadAvatar(@RequestPart("file") MultipartFile file);

    /**
     * 上传视频
     *
     * @param file 视频文件
     * @return 视频URL
     */
    @PostMapping("/upload/video")
    BaseResponse<String> uploadVideo(@RequestPart("file") MultipartFile file);

    /**
     * 上传文档
     *
     * @param file 文档文件
     * @return 文档URL
     */
    @PostMapping("/upload/document")
    BaseResponse<String> uploadDocument(@RequestPart("file") MultipartFile file);

    /**
     * 上传课程资料
     *
     * @param file 课程资料文件
     * @return 资料URL
     */
    @PostMapping("/upload/material")
    BaseResponse<String> uploadMaterial(@RequestPart("file") MultipartFile file);
} 