package com.ubanillx.smartclassbackenddailyarticle.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.ubanillx.smartclassbackendmodel.model.entity.DailyArticle;
import com.ubanillx.smartclassbackendmodel.model.entity.UserDailyArticle;
import org.apache.ibatis.annotations.Param;

/**
* @author liulo
* @description 针对表【user_daily_article(用户与每日文章关联)】的数据库操作Mapper
* @createDate 2025-03-20 14:25:20
* @Entity com.ubanillx.smartclassbackendmodel.model.entity.UserDailyArticle
*/
public interface UserDailyArticleMapper extends BaseMapper<UserDailyArticle> {

    /**
     * 获取用户收藏的文章分页列表
     * 
     * @param page
     * @param queryWrapper
     * @param favourUserId
     * @return
     */
    Page<DailyArticle> listFavourArticleByPage(IPage<DailyArticle> page,
                                               @Param(Constants.WRAPPER) Wrapper<DailyArticle> queryWrapper,
                                               @Param("favourUserId") long favourUserId);
            
    /**
     * 获取用户点赞的文章分页列表
     * 
     * @param page
     * @param queryWrapper
     * @param thumbUserId
     * @return
     */
    Page<DailyArticle> listThumbArticleByPage(IPage<DailyArticle> page, 
            @Param(Constants.WRAPPER) Wrapper<DailyArticle> queryWrapper, 
            @Param("thumbUserId") long thumbUserId);
}




