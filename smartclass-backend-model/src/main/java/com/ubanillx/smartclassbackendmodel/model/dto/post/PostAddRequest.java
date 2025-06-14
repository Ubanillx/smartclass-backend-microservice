package com.ubanillx.smartclassbackendmodel.model.dto.post;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建请求
*/
@Data
public class PostAddRequest implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;
    
    /**
     * 客户端IP地址
     */
    private String clientIp;
    
    /**
     * 帖子类型，如学习/生活/技巧
     */
    private String type;

    private static final long serialVersionUID = 1L;
}