package com.ubanillx.smartclassbackendmodel.model.dto.postfavour;

import com.ubanillx.smartclassbackendcommon.common.PageRequest;import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 帖子收藏查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostFavourQueryRequest extends PageRequest implements Serializable {
    
    /**
     * 帖子id
     */
    private Long postId;
    
    /**
     * 用户id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}