package com.ubanillx.smartclassbackendannouncement.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclassbackendannouncement.service.AnnouncementService;
import com.ubanillx.smartclassbackendcommon.annotation.AuthCheck;
import com.ubanillx.smartclassbackendcommon.common.BaseResponse;
import com.ubanillx.smartclassbackendcommon.common.ErrorCode;
import com.ubanillx.smartclassbackendcommon.common.ResultUtils;
import com.ubanillx.smartclassbackendcommon.constant.UserConstant;
import com.ubanillx.smartclassbackendcommon.exception.BusinessException;
import com.ubanillx.smartclassbackendcommon.exception.ThrowUtils;
import com.ubanillx.smartclassbackendmodel.model.dto.announcement.AnnouncementAddRequest;
import com.ubanillx.smartclassbackendmodel.model.dto.announcement.AnnouncementQueryRequest;
import com.ubanillx.smartclassbackendmodel.model.dto.announcement.AnnouncementUpdateRequest;
import com.ubanillx.smartclassbackendmodel.model.entity.Announcement;
import com.ubanillx.smartclassbackendmodel.model.entity.User;
import com.ubanillx.smartclassbackendmodel.model.vo.AnnouncementVO;
import com.ubanillx.smartclassbackendserviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 系统公告接口
 */
@RestController
@RequestMapping("/announcements")
@Slf4j
public class AnnouncementController {

    @Resource
    private AnnouncementService announcementService;

    @Resource
    private UserFeignClient userService;

    // region 增删改查

    /**
     * 创建公告（仅管理员）
     *
     * @param announcementAddRequest 公告创建请求体
     * @param request HTTP请求
     * @return 新创建的公告ID
     */
    @PostMapping("")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addAnnouncement(@RequestBody AnnouncementAddRequest announcementAddRequest,
                                              HttpServletRequest request) {
        if (announcementAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(announcementAddRequest, announcement);
        User loginUser = userService.getLoginUser(request);
        Long adminId = loginUser.getId();
        long id = announcementService.addAnnouncement(announcement, adminId);
        return ResultUtils.success(id);
    }

    /**
     * 删除公告（仅管理员）
     *
     * @param id 要删除的公告ID
     * @return 删除结果，true表示成功，false表示失败
     */
    @DeleteMapping("/{id}")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteAnnouncement(@PathVariable long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        Announcement oldAnnouncement = announcementService.getById(id);
        ThrowUtils.throwIf(oldAnnouncement == null, ErrorCode.NOT_FOUND_ERROR);
        boolean b = announcementService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新公告（仅管理员）
     *
     * @param id 要更新的公告ID
     * @param announcementUpdateRequest 公告更新请求体，包含要更新的公告信息
     * @return 更新结果，true表示成功，false表示失败
     */
    @PutMapping("/{id}/admin")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateAnnouncement(@PathVariable long id,
                                                 @RequestBody AnnouncementUpdateRequest announcementUpdateRequest) {
        if (announcementUpdateRequest == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 确保ID一致
        announcementUpdateRequest.setId(id);
        
        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(announcementUpdateRequest, announcement);
        // 判断是否存在
        Announcement oldAnnouncement = announcementService.getById(id);
        ThrowUtils.throwIf(oldAnnouncement == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = announcementService.updateById(announcement);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取公告（封装类）
     *
     * @param id 公告ID
     * @param request HTTP请求
     * @return 公告信息（VO对象）
     */
    @GetMapping("/{id}")
    public BaseResponse<AnnouncementVO> getAnnouncementVOById(@PathVariable long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Announcement announcement = announcementService.getById(id);
        if (announcement == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 获取封装
        AnnouncementVO announcementVO = announcementService.getAnnouncementVO(announcement);
        
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 如果用户已登录，查询是否已读
        if (loginUser != null) {
            boolean hasRead = announcementService.hasReadAnnouncement(id, loginUser.getId());
            announcementVO.setHasRead(hasRead);
            
            // 如果未读，增加公告查看次数并标记为已读
            if (!hasRead) {
                announcementService.increaseViewCount(id);
                announcementService.readAnnouncement(id, loginUser.getId());
            }
        } else {
            // 未登录用户增加查看次数
            announcementService.increaseViewCount(id);
        }
        
        return ResultUtils.success(announcementVO);
    }

    /**
     * 分页获取公告列表（仅管理员）
     *
     * @param announcementQueryRequest 公告查询请求体，包含分页参数和查询条件
     * @return 分页公告信息
     */
    @GetMapping("/admin/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Announcement>> listAnnouncementByPage(AnnouncementQueryRequest announcementQueryRequest) {
        if (announcementQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = announcementQueryRequest.getCurrent();
        long size = announcementQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<Announcement> queryWrapper = announcementService.getQueryWrapper(announcementQueryRequest);
        Page<Announcement> announcementPage = announcementService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(announcementPage);
    }

    /**
     * 分页获取公告列表（封装类）
     *
     * @param announcementQueryRequest 公告查询请求体，包含分页参数和查询条件，设置isValid=true可以只获取有效公告
     * @param request HTTP请求
     * @return 分页公告信息（VO对象）
     */
    @GetMapping("/page")
    public BaseResponse<Page<AnnouncementVO>> listAnnouncementVOByPage(AnnouncementQueryRequest announcementQueryRequest,
                                                                  HttpServletRequest request) {
        if (announcementQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = announcementQueryRequest.getCurrent();
        long size = announcementQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Announcement> announcementPage = announcementService.page(new Page<>(current, size),
                announcementService.getQueryWrapper(announcementQueryRequest));
        
        List<AnnouncementVO> announcementVOList = announcementService.getAnnouncementVO(announcementPage.getRecords());
        Page<AnnouncementVO> announcementVOPage = new Page<>(current, size, announcementPage.getTotal());
        announcementVOPage.setRecords(announcementVOList);
        
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 如果用户已登录，查询是否已读
        if (loginUser != null) {
            Long userId = loginUser.getId();
            for (AnnouncementVO announcementVO : announcementVOList) {
                boolean hasRead = announcementService.hasReadAnnouncement(announcementVO.getId(), userId);
                announcementVO.setHasRead(hasRead);
            }
        }
        
        return ResultUtils.success(announcementVOPage);
    }

    /**
     * 标记公告为已读
     *
     * @param id 公告ID
     * @param request HTTP请求
     * @return 标记结果，true表示成功，false表示失败
     */
    @PostMapping("/{id}/read")
    public BaseResponse<Boolean> readAnnouncement(@PathVariable long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 判断是否已读
        boolean hasRead = announcementService.hasReadAnnouncement(id, loginUser.getId());
        
        // 如果未读，则增加查看次数
        if (!hasRead) {
            announcementService.increaseViewCount(id);
        }
        
        // 标记为已读
        boolean result = announcementService.readAnnouncement(id, loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 检查公告是否已读
     *
     * @param id 公告ID
     * @param request HTTP请求
     * @return 是否已读，true表示已读，false表示未读
     */
    @GetMapping("/{id}/has-read")
    public BaseResponse<Boolean> hasReadAnnouncement(@PathVariable long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 查询是否已读
        boolean result = announcementService.hasReadAnnouncement(id, loginUser.getId());
        return ResultUtils.success(result);
    }
} 