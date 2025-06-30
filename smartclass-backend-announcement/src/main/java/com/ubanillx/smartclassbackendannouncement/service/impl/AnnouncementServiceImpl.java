package com.ubanillx.smartclassbackendannouncement.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclassbackendannouncement.mapper.AnnouncementMapper;
import com.ubanillx.smartclassbackendannouncement.mapper.UserAnnouncementReaderMapper;
import com.ubanillx.smartclassbackendannouncement.service.AnnouncementService;
import com.ubanillx.smartclassbackendcommon.common.ErrorCode;
import com.ubanillx.smartclassbackendcommon.constant.CommonConstant;
import com.ubanillx.smartclassbackendcommon.exception.BusinessException;
import com.ubanillx.smartclassbackendcommon.utils.SqlUtils;
import com.ubanillx.smartclassbackendmodel.model.dto.announcement.AnnouncementQueryRequest;
import com.ubanillx.smartclassbackendmodel.model.entity.Announcement;
import com.ubanillx.smartclassbackendmodel.model.entity.UserAnnouncementReader;
import com.ubanillx.smartclassbackendmodel.model.vo.AnnouncementVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统公告服务实现
 */
@Service
@Slf4j
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement>
        implements AnnouncementService {

    @Resource
    private UserAnnouncementReaderMapper userAnnouncementReaderMapper;

    @Override
    public long addAnnouncement(Announcement announcement, Long adminId) {
        if (announcement == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (StringUtils.isBlank(announcement.getTitle())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "公告标题不能为空");
        }
        if (StringUtils.isBlank(announcement.getContent())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "公告内容不能为空");
        }
        if (announcement.getPriority() == null) {
            announcement.setPriority(0); // 默认优先级为0
        }
        if (announcement.getStatus() == null) {
            announcement.setStatus(0); // 默认状态为草稿
        }
        announcement.setAdminId(adminId);
        announcement.setViewCount(0); // 初始化查看次数为0
        boolean result = this.save(announcement);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加失败");
        }
        return announcement.getId();
    }

    @Override
    public QueryWrapper<Announcement> getQueryWrapper(AnnouncementQueryRequest announcementQueryRequest) {
        if (announcementQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = announcementQueryRequest.getId();
        String title = announcementQueryRequest.getTitle();
        String content = announcementQueryRequest.getContent();
        Integer priority = announcementQueryRequest.getPriority();
        Integer status = announcementQueryRequest.getStatus();
        Date startTime = announcementQueryRequest.getStartTime();
        Date endTime = announcementQueryRequest.getEndTime();
        String coverImage = announcementQueryRequest.getCoverImage();
        Long adminId = announcementQueryRequest.getAdminId();
        Date createTime = announcementQueryRequest.getCreateTime();
        Boolean isValid = announcementQueryRequest.getIsValid();
        String sortField = announcementQueryRequest.getSortField();
        String sortOrder = announcementQueryRequest.getSortOrder();

        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.eq(priority != null, "priority", priority);
        queryWrapper.eq(status != null && !Boolean.TRUE.equals(isValid), "status", status);
        queryWrapper.ge(startTime != null && !Boolean.TRUE.equals(isValid), "startTime", startTime);
        queryWrapper.le(endTime != null && !Boolean.TRUE.equals(isValid), "endTime", endTime);
        queryWrapper.like(StringUtils.isNotBlank(coverImage), "coverImage", coverImage);
        queryWrapper.eq(adminId != null, "adminId", adminId);
        queryWrapper.eq(createTime != null, "createTime", createTime);
        queryWrapper.eq("isDelete", 0);
        
        // 如果isValid为true，添加有效公告的查询条件
        if (Boolean.TRUE.equals(isValid)) {
            queryWrapper.eq("status", 1); // 已发布状态
            queryWrapper.le("startTime", new Date()); // 开始时间小于等于当前时间
            queryWrapper.ge("endTime", new Date()); // 结束时间大于等于当前时间
            // 按优先级和创建时间降序排序
            queryWrapper.orderByDesc("priority", "createTime");
        } else {
            queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                    sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                    sortField);
        }
        
        return queryWrapper;
    }

    @Override
    public AnnouncementVO getAnnouncementVO(Announcement announcement) {
        if (announcement == null) {
            return null;
        }
        AnnouncementVO announcementVO = new AnnouncementVO();
        BeanUtils.copyProperties(announcement, announcementVO);
        return announcementVO;
    }

    @Override
    public List<AnnouncementVO> getAnnouncementVO(List<Announcement> announcementList) {
        if (CollUtil.isEmpty(announcementList)) {
            return new ArrayList<>();
        }
        return announcementList.stream().map(this::getAnnouncementVO).collect(Collectors.toList());
    }

    @Override
    public Page<AnnouncementVO> listValidAnnouncements(long current, long size) {
        // 查询有效的公告（已发布、在有效期内的公告）
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1); // 已发布状态
        queryWrapper.le("startTime", new Date()); // 开始时间小于等于当前时间
        queryWrapper.ge("endTime", new Date()); // 结束时间大于等于当前时间
        queryWrapper.eq("isDelete", 0);
        queryWrapper.orderByDesc("priority", "createTime"); // 按优先级和创建时间降序排序

        Page<Announcement> announcementPage = this.page(new Page<>(current, size), queryWrapper);
        List<AnnouncementVO> announcementVOList = getAnnouncementVO(announcementPage.getRecords());
        
        Page<AnnouncementVO> announcementVOPage = new Page<>(current, size, announcementPage.getTotal());
        announcementVOPage.setRecords(announcementVOList);
        return announcementVOPage;
    }

    @Override
    public boolean increaseViewCount(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        UpdateWrapper<Announcement> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.setSql("viewCount = viewCount + 1");
        return this.update(updateWrapper);
    }

    @Override
    public boolean readAnnouncement(Long announcementId, Long userId) {
        if (announcementId == null || announcementId <= 0 || userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }

        // 检查公告是否存在
        Announcement announcement = this.getById(announcementId);
        if (announcement == null || announcement.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "公告不存在");
        }

        // 检查是否已经阅读
        if (hasReadAnnouncement(announcementId, userId)) {
            return true;
        }

        // 记录阅读
        UserAnnouncementReader userAnnouncementReader = new UserAnnouncementReader();
        userAnnouncementReader.setUserId(userId);
        userAnnouncementReader.setAnnouncementId(announcementId);
        userAnnouncementReader.setReadTime(new Date());
        userAnnouncementReader.setCreateTime(new Date());
        return userAnnouncementReaderMapper.insert(userAnnouncementReader) > 0;
    }

    @Override
    public boolean hasReadAnnouncement(Long announcementId, Long userId) {
        if (announcementId == null || announcementId <= 0 || userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }

        QueryWrapper<UserAnnouncementReader> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("announcementId", announcementId);
        queryWrapper.eq("userId", userId);
        return userAnnouncementReaderMapper.selectCount(queryWrapper) > 0;
    }
} 