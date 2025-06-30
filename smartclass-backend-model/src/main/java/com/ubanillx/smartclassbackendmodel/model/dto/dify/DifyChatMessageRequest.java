package com.ubanillx.smartclassbackendmodel.model.dto.dify;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * Dify聊天消息请求DTO
 * 用于sendChatMessage和sendChatMessageStreaming方法
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DifyChatMessageRequest implements Serializable {
    
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * AI分身ID
     */
    @NotNull(message = "AI分身ID不能为空")
    private Long aiAvatarId;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String content;
    
    /**
     * Dify API基础URL
     */
    @NotBlank(message = "API基础URL不能为空")
    private String baseUrl;
    
    /**
     * AI分身授权token
     */
    @NotBlank(message = "授权token不能为空")
    private String avatarAuth;
    
    private static final long serialVersionUID = 1L;
} 