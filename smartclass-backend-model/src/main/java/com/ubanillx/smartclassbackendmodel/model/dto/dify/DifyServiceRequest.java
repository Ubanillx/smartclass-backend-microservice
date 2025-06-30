package com.ubanillx.smartclassbackendmodel.model.dto.dify;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Dify服务通用请求DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DifyServiceRequest implements Serializable {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * AI分身ID
     */
    private Long aiAvatarId;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * Dify API基础URL
     */
    private String baseUrl;
    
    /**
     * AI分身授权token
     */
    private String avatarAuth;
    
    /**
     * 是否结束聊天
     */
    private Boolean endChat;
    
    private static final long serialVersionUID = 1L;
} 