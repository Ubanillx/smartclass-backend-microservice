package com.ubanillx.smartclassbackendmodel.model.dto.dify;

import lombok.Data;

import java.io.Serializable;

/**
 * Dify会话总结响应DTO
 */
@Data
public class DifySessionSummaryResponse implements Serializable {
    
    /**
     * 会话总结内容
     */
    private String summary;
    
    /**
     * 会话ID
     */
    private String conversation_id;
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 错误代码
     */
    private String code;
    
    /**
     * 错误信息
     */
    private String message;
    
    private static final long serialVersionUID = 1L;
} 