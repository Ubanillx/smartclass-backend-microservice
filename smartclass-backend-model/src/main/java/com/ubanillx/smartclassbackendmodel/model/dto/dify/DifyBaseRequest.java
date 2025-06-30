package com.ubanillx.smartclassbackendmodel.model.dto.dify;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Dify基础请求DTO，包含通用的API配置参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DifyBaseRequest implements Serializable {
    
    /**
     * Dify API基础URL
     */
    private String baseUrl;
    
    /**
     * AI分身授权token
     */
    private String avatarAuth;
    
    private static final long serialVersionUID = 1L;
} 