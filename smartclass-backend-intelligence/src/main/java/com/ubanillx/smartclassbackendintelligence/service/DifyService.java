package com.ubanillx.smartclassbackendintelligence.service;

import com.ubanillx.smartclassbackendmodel.model.dto.dify.*;
import com.ubanillx.smartclassbackendmodel.model.entity.AiAvatarChatHistory;
import com.ubanillx.smartclassbackendmodel.model.vo.ChatMessageVO;
import com.ubanillx.smartclassbackendserviceclient.service.UserFeignClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * DifyAPI服务接口
 */
public interface DifyService {

    /**
     * 发送聊天消息并获取回复
     *
     * @param request      聊天消息请求DTO
     * @return             保存的消息记录
     */
    AiAvatarChatHistory sendChatMessage(DifyChatMessageRequest request);
    
    /**
     * 发送聊天消息流式处理
     *
     * @param request      聊天消息请求DTO
     * @param callback     处理响应块的回调函数
     * @return             保存的消息记录
     */
    AiAvatarChatHistory sendChatMessageStreaming(DifyChatMessageRequest request, DifyStreamCallback callback);
    
    /**
     * 处理常规消息发送请求的完整业务逻辑
     *
     * @param request               处理消息请求DTO
     * @param chatHistoryService    聊天历史服务
     * @param aiAvatarService       AI分身服务
     * @param userService           用户服务
     * @return                      处理结果
     */
    ChatMessageVO handleSendMessageRequest(DifyHandleMessageRequest request, AiAvatarChatHistoryService chatHistoryService,
                                           AiAvatarService aiAvatarService, UserFeignClient userService);
    
    /**
     * 处理流式消息发送请求的完整业务逻辑
     *
     * @param request               流式消息请求DTO
     * @param chatHistoryService    聊天历史服务
     * @param aiAvatarService       AI分身服务
     * @return                      SSE事件发射器
     */
    SseEmitter handleStreamMessageRequest(DifyStreamMessageRequest request, AiAvatarChatHistoryService chatHistoryService,
                                        AiAvatarService aiAvatarService);
    
    /**
     * 获取会话总结
     *
     * @param request      会话总结请求DTO
     * @return             会话总结内容
     */
    String getSessionSummary(DifySessionSummaryRequest request);
    
    /**
     * 删除Dify会话
     *
     * @param request      删除会话请求DTO
     * @return             是否删除成功
     */
    boolean deleteConversation(DifyDeleteConversationRequest request);
    
    /**
     * 停止流式响应
     *
     * @param request      停止流式响应请求DTO
     * @return             是否成功
     */
    boolean stopStreamingResponse(DifyStopStreamingRequest request);
    
    /**
     * 用于处理Dify流式响应的回调接口
     */
    interface DifyStreamCallback {
        /**
         * 处理单个响应块
         * @param chunk 响应块内容
         */
        void onMessage(String chunk);
        
        /**
         * 所有响应块处理完成
         * @param fullResponse 完整响应内容
         */
        void onComplete(String fullResponse);
        
        /**
         * 处理过程中出现错误
         * @param error 错误信息
         */
        void onError(Throwable error);
    }
} 