package com.ubanillx.smartclassbackendmodel.model.dto.postfavour;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 帖子收藏添加请求
 */
@Data
public class PostFavourAddRequest implements Serializable {

    /**
     * 帖子id
     */
    @NotNull(message = "帖子id不能为空")
    private Long postId;

    private static final long serialVersionUID = 1L;
}