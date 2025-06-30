package com.ubanillx.smartclassbackendmodel.model.dto.dify;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Dify停止流式响应请求DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DifyStopStreamingRequest implements Serializable {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 任务ID，可从流式返回Chunk中获取
     */
    private String taskId;
    
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