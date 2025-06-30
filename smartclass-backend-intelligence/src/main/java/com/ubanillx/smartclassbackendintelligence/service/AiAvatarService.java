package com.ubanillx.smartclassbackendintelligence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclassbackendmodel.model.dto.aiavatar.AiAvatarUpdateRequest;
import com.ubanillx.smartclassbackendmodel.model.entity.AiAvatar;
import com.ubanillx.smartclassbackendmodel.model.vo.AiAvatarBriefVO;


import java.util.List;

/**
* @author liulo
* @description 针对表【ai_avatar(AI分身)】的数据库操作Service
* @createDate 2025-03-18 23:08:38
*/
public interface AiAvatarService extends IService<AiAvatar> {

    /**
     * 获取所有AI分身的简要信息列表
     * 
     * @return AI分身简要信息列表
     */
    List<AiAvatarBriefVO> listAllAiAvatarBrief();
    
    /**
     * 从更新请求创建增量更新的AI分身实体
     * 
     * @param updateRequest 更新请求
     * @return 更新的AI分身实体
     */
    AiAvatar createUpdateEntity(AiAvatarUpdateRequest updateRequest);
}
