package com.ubanillx.smartclassbackendcircle.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclassbackendmodel.model.dto.post.PostQueryRequest;
import com.ubanillx.smartclassbackendmodel.model.entity.Post;
import com.ubanillx.smartclassbackendmodel.model.vo.PostVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 帖子服务
*/
public interface PostService extends IService<Post> {

    /**
     * 校验
     *
     * @param post
     * @param add
     */
    void validPost(Post post, boolean add);

    /**
     * 获取查询条件
     *
     * @param postQueryRequest
     * @return
     */
    QueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest);

    /**
     * 从 ES 查询
     *
     * @param postQueryRequest
     * @return
     */
    Page<Post> searchFromEs(PostQueryRequest postQueryRequest);
    
    /**
     * 从 ES 查询（仅搜索词）
     *
     * @param searchText 搜索关键词
     * @return 帖子分页列表
     */
    Page<Post> searchFromEs(String searchText);

    /**
     * 获取帖子封装
     *
     * @param post
     * @param request
     * @return
     */
    PostVO getPostVO(Post post, HttpServletRequest request);

    /**
     * 分页获取帖子封装
     *
     * @param postPage
     * @param request
     * @return
     */
    Page<PostVO> getPostVOPage(Page<Post> postPage, HttpServletRequest request);
    
    /**
     * 保存帖子，同时同步到ES
     *
     * @param post
     * @return
     */
    boolean savePost(Post post);
    
    /**
     * 更新帖子，同时更新ES
     *
     * @param post
     * @return
     */
    boolean updatePost(Post post);
    
    /**
     * 删除帖子，同时从ES删除
     *
     * @param id
     * @return
     */
    boolean deletePost(Long id);
}
