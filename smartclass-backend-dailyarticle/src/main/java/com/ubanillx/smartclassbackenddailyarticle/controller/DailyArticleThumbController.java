package com.ubanillx.smartclassbackenddailyarticle.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclassbackendcommon.common.BaseResponse;
import com.ubanillx.smartclassbackendcommon.common.ErrorCode;
import com.ubanillx.smartclassbackendcommon.common.ResultUtils;
import com.ubanillx.smartclassbackendcommon.exception.BusinessException;
import com.ubanillx.smartclassbackenddailyarticle.service.DailyArticleService;
import com.ubanillx.smartclassbackenddailyarticle.service.DailyArticleThumbService;
import com.ubanillx.smartclassbackendmodel.model.dto.dailyarticle.DailyArticleQueryRequest;
import com.ubanillx.smartclassbackendmodel.model.entity.DailyArticle;
import com.ubanillx.smartclassbackendmodel.model.entity.User;
import com.ubanillx.smartclassbackendmodel.model.vo.DailyArticleVO;
import com.ubanillx.smartclassbackendserviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 每日文章点赞接口
 */
@RestController
@RequestMapping("/daily-articles")
@Slf4j
public class DailyArticleThumbController {

    @Resource
    private DailyArticleThumbService dailyArticleThumbService;

    @Resource
    private DailyArticleService dailyArticleService;

    @Resource
    private UserFeignClient userService;

    /**
     * 点赞文章
     *
     * @param articleId 文章ID
     * @param request HTTP请求对象，用于获取当前登录用户信息
     * @return 1-点赞成功；0-操作失败
     */
    @PostMapping("/{articleId}/thumbs")
    public BaseResponse<Integer> thumbArticle(@PathVariable("articleId") long articleId,
                                              HttpServletRequest request) {
        if (articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 执行点赞操作
        int result = dailyArticleThumbService.thumbArticle(articleId, loginUser);
        return ResultUtils.success(result);
    }
    
    /**
     * 取消点赞文章
     *
     * @param articleId 文章ID
     * @param request HTTP请求对象，用于获取当前登录用户信息
     * @return 操作结果，1-取消成功，0-操作失败
     */
    @DeleteMapping("/{articleId}/thumbs")
    public BaseResponse<Integer> cancelArticleThumb(@PathVariable("articleId") long articleId,
                                               HttpServletRequest request) {
        if (articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 执行取消点赞
        int result = dailyArticleThumbService.cancelArticleThumb(articleId, loginUser.getId());
        return ResultUtils.success(result);
    }
    
    /**
     * 获取用户点赞的文章列表
     *
     * @param dailyArticleQueryRequest 查询参数，包含分页信息、排序条件等
     * @param request HTTP请求对象，用于获取当前登录用户信息
     * @return 分页后的文章视图对象列表
     */
    @GetMapping("/thumbs/me/page")
    public BaseResponse<Page<DailyArticleVO>> listMyThumbArticleByPage(DailyArticleQueryRequest dailyArticleQueryRequest,
                                                                       HttpServletRequest request) {
        if (dailyArticleQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        dailyArticleQueryRequest.setPageSize(Math.min(20, dailyArticleQueryRequest.getPageSize()));
        long current = dailyArticleQueryRequest.getCurrent();
        long size = dailyArticleQueryRequest.getPageSize();
        // 构造查询条件
        QueryWrapper<DailyArticle> queryWrapper = dailyArticleService.getQueryWrapper(dailyArticleQueryRequest);
        // 获取点赞文章分页数据
        Page<DailyArticleVO> articlePage = dailyArticleThumbService.listThumbArticleByPage(new Page<>(current, size),
                queryWrapper, loginUser.getId());
        return ResultUtils.success(articlePage);
    }

    /**
     * 查询当前用户是否点赞了文章
     *
     * @param articleId 文章ID
     * @param request HTTP请求对象，用于获取当前登录用户信息
     * @return 是否点赞
     */
    @GetMapping("/{articleId}/thumbs/status")
    public BaseResponse<Boolean> isThumbArticle(@PathVariable("articleId") long articleId,
                                              HttpServletRequest request) {
        if (articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 查询是否点赞
        boolean result = dailyArticleThumbService.isThumbArticle(articleId, loginUser.getId());
        return ResultUtils.success(result);
    }
} 