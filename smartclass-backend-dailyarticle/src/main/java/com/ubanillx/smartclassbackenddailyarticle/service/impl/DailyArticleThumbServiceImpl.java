package com.ubanillx.smartclassbackenddailyarticle.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclassbackendcommon.common.ErrorCode;
import com.ubanillx.smartclassbackendcommon.exception.BusinessException;
import com.ubanillx.smartclassbackenddailyarticle.mapper.UserDailyArticleMapper;
import com.ubanillx.smartclassbackenddailyarticle.service.DailyArticleService;
import com.ubanillx.smartclassbackenddailyarticle.service.DailyArticleThumbService;
import com.ubanillx.smartclassbackendmodel.model.entity.DailyArticle;
import com.ubanillx.smartclassbackendmodel.model.entity.User;
import com.ubanillx.smartclassbackendmodel.model.entity.UserDailyArticle;
import com.ubanillx.smartclassbackendmodel.model.vo.DailyArticleVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 每日文章点赞服务实现
*/
@Service
public class DailyArticleThumbServiceImpl extends ServiceImpl<UserDailyArticleMapper, UserDailyArticle>
        implements DailyArticleThumbService {

    @Resource
    private DailyArticleService dailyArticleService;

    
    /**
     * 点赞文章
     *
     * @param articleId 文章id
     * @param loginUser 当前登录用户
     * @return 1-点赞成功，0-操作失败
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int thumbArticle(long articleId, User loginUser) {
        // 判断文章是否存在
        DailyArticle article = dailyArticleService.getById(articleId);
        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        }
        
        // 获取用户ID
        long userId = loginUser.getId();
        
        // 查询用户与文章的关联记录
        QueryWrapper<UserDailyArticle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("articleId", articleId);
        UserDailyArticle userDailyArticle = this.getOne(queryWrapper);
        
        // 当前时间
        Date now = new Date();
        
        // 如果关联记录不存在，创建新记录
        if (userDailyArticle == null) {
            userDailyArticle = new UserDailyArticle();
            userDailyArticle.setUserId(userId);
            userDailyArticle.setArticleId(articleId);
            userDailyArticle.setIsLiked(1); // 设置为已点赞
            userDailyArticle.setLikeTime(now);
            userDailyArticle.setCreateTime(now);
            boolean result = this.save(userDailyArticle);
            if (result) {
                // 更新文章点赞数 +1
                dailyArticleService.increaseLikeCount(articleId);
                return 1;
            } else {
                return 0;
            }
        }
        
        // 如果关联记录存在但未点赞，则设置为点赞
        Integer isLiked = userDailyArticle.getIsLiked();
        if (isLiked == null || isLiked == 0) {
            userDailyArticle.setIsLiked(1);
            userDailyArticle.setLikeTime(now);
            userDailyArticle.setUpdateTime(now);
            boolean result = this.updateById(userDailyArticle);
            if (result) {
                // 更新文章点赞数 +1
                dailyArticleService.increaseLikeCount(articleId);
                return 1;
            }
        }
        
        // 如果已经点赞，不做操作
        return 0;
    }
    
    /**
     * 取消点赞文章
     *
     * @param articleId 文章id
     * @param userId 用户id
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancelArticleThumb(long articleId, long userId) {
        // 判断文章是否存在
        DailyArticle article = dailyArticleService.getById(articleId);
        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        }
        
        // 查询用户与文章的关联记录
        QueryWrapper<UserDailyArticle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("articleId", articleId);
        UserDailyArticle userDailyArticle = this.getOne(queryWrapper);
        
        // 如果关联记录不存在或已经是未点赞状态，返回失败
        if (userDailyArticle == null || userDailyArticle.getIsLiked() == 0) {
            return 0;
        }
        
        // 设置为未点赞
        userDailyArticle.setIsLiked(0);
        userDailyArticle.setLikeTime(null);
        userDailyArticle.setUpdateTime(new Date());
        boolean result = this.updateById(userDailyArticle);
        if (result) {
            // 更新文章点赞数 -1
            dailyArticleService.decreaseLikeCount(articleId);
            return 1;
        }
        return 0;
    }
    
    /**
     * 分页获取用户点赞的每日文章列表
     *
     * @param page
     * @param queryWrapper
     * @param thumbUserId
     * @return
     */
    @Override
    public Page<DailyArticleVO> listThumbArticleByPage(IPage<DailyArticle> page, Wrapper<DailyArticle> queryWrapper, long thumbUserId) {
        if (thumbUserId <= 0) {
            return new Page<>();
        }
        
        // 使用Mapper中的自定义方法查询点赞的文章
        Page<DailyArticle> articlePage = baseMapper.listThumbArticleByPage(page, queryWrapper, thumbUserId);
        
        // 转换为VO对象
        List<DailyArticleVO> articleVOList = dailyArticleService.getDailyArticleVO(articlePage.getRecords());
        
        // 构建返回结果
        Page<DailyArticleVO> articleVOPage = new Page<>(page.getCurrent(), page.getSize(), articlePage.getTotal());
        articleVOPage.setRecords(articleVOList);
        
        return articleVOPage;
    }

    /**
     * 判断用户是否点赞了文章
     *
     * @param articleId
     * @param userId
     * @return
     */
    @Override
    public boolean isThumbArticle(long articleId, long userId) {
        // 查询是否存在点赞记录
        QueryWrapper<UserDailyArticle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("articleId", articleId);
        queryWrapper.eq("isLiked", 1);
        return this.count(queryWrapper) > 0;
    }
} 