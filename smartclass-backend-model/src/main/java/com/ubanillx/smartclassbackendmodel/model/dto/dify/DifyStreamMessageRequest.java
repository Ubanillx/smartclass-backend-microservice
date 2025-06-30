package com.ubanillx.smartclassbackendmodel.model.dto.dify;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * Dify流式消息请求DTO
 * 用于handleStreamMessageRequest方法，专门处理流式消息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DifyStreamMessageRequest implements Serializable {
    
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
    
    private static final long serialVersionUID = 1L;
} 