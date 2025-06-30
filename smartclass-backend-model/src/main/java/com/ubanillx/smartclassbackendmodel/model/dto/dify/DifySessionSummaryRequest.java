package com.ubanillx.smartclassbackendmodel.model.dto.dify;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Dify会话总结请求DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DifySessionSummaryRequest implements Serializable {
    
    /**
     * 会话ID
     */
    private String sessionId;
    
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