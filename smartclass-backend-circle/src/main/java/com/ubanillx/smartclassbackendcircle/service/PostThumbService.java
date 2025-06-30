 package com.ubanillx.smartclassbackendcircle.service;

 import com.baomidou.mybatisplus.extension.service.IService;
 import com.ubanillx.smartclassbackendmodel.model.entity.PostThumb;

/**
 * 帖子点赞服务
*/
public interface PostThumbService extends IService<PostThumb> {

    /**
     * 添加帖子点赞
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean addPostThumb(long postId, long userId);

    /**
     * 取消帖子点赞
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean cancelPostThumb(long postId, long userId);

    /**
     * 判断用户是否已点赞帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    boolean hasThumb(long postId, long userId);
}