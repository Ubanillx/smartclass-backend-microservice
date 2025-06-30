package com.ubanillx.smartclassbackendmodel.model.dto.dify;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Dify删除会话请求DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DifyDeleteConversationRequest implements Serializable {
    
    /**
     * 用户ID
     */
    private Long userId;
    
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