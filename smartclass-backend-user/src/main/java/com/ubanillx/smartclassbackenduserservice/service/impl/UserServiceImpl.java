package com.ubanillx.smartclassbackenduserservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclassbackendcommon.common.ErrorCode;
import com.ubanillx.smartclassbackendcommon.constant.CommonConstant;
import com.ubanillx.smartclassbackendcommon.exception.BusinessException;
import com.ubanillx.smartclassbackendcommon.utils.SqlUtils;
import com.ubanillx.smartclassbackendmodel.model.dto.user.UserQueryRequest;
import com.ubanillx.smartclassbackendmodel.model.entity.User;
import com.ubanillx.smartclassbackendmodel.model.enums.UserRoleEnum;
import com.ubanillx.smartclassbackendmodel.model.vo.LoginUserVO;
import com.ubanillx.smartclassbackendmodel.model.vo.UserVO;
import com.ubanillx.smartclassbackenduserservice.mapper.UserMapper;
import com.ubanillx.smartclassbackenduserservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.ubanillx.smartclassbackendcommon.constant.UserConstant.SALT;
import static com.ubanillx.smartclassbackendcommon.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    // 新增一个用于注册时加锁的 map
    private final ConcurrentHashMap<String, Object> registerLockMap = new ConcurrentHashMap<>();

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        // 获取或创建锁对象
        Object lock = registerLockMap.computeIfAbsent(userAccount, k -> new Object());
        synchronized (lock) {
            try {
                // 账户不能重复
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("userAccount", userAccount);
                long count = this.baseMapper.selectCount(queryWrapper);
                if (count > 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
                }

                // 2. 加密
                String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

                // 3. 插入数据
                User user = new User();
                user.setUserAccount(userAccount);
                user.setUserPassword(encryptPassword);
                boolean saveResult = this.save(user);
                if (!saveResult) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
                }
                return user.getId();
            } finally {
                // 注册完成后移除锁对象，避免内存泄漏
                registerLockMap.remove(userAccount);
            }
        }
    }

    // 在类中定义锁 map
    private final Map<String, Object> phoneRegisterLockMap = new ConcurrentHashMap<>();

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long userRegisterByPhone(String userPhone, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userPhone, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (!userPhone.matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号格式错误");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        // 加锁注册流程
        Object lock = phoneRegisterLockMap.computeIfAbsent(userPhone, k -> new Object());
        synchronized (lock) {
            try {
                // 手机号不能重复
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("userPhone", userPhone);
                long count = this.baseMapper.selectCount(queryWrapper);
                if (count > 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "该手机号已被注册");
                }

                // 随机生成账号（最多尝试10次）
                String userAccount = null;
                int maxAttempts = 10;
                boolean foundUnique = false;
                QueryWrapper<User> accountQueryWrapper = new QueryWrapper<>();

                for (int i = 0; i < maxAttempts; i++) {
                    userAccount = generateRandomAccount();
                    accountQueryWrapper.clear();
                    accountQueryWrapper.eq("userAccount", userAccount);
                    long accountCount = this.baseMapper.selectCount(accountQueryWrapper);
                    if (accountCount == 0) {
                        foundUnique = true;
                        break;
                    }
                }

                if (!foundUnique) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "无法生成唯一账号，请稍后重试");
                }

                // 加密密码
                String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

                // 插入数据
                User user = new User();
                user.setUserAccount(userAccount);
                user.setUserPhone(userPhone);
                user.setUserPassword(encryptPassword);
                boolean saveResult = this.save(user);
                if (!saveResult) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
                }
                return user.getId();

            } finally {
                phoneRegisterLockMap.remove(userPhone);
            }
        }
    }


    /**
     * 生成指定长度的随机字符串作为账号
     *
     * @return 随机生成的账号
     */
    private String generateRandomAccount() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {

            int index = ThreadLocalRandom.current().nextInt(characters.length());

            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    @Override
    public LoginUserVO userLoginByPhone(String userPhone, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userPhone, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (!userPhone.matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号格式错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userPhone", userPhone);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userPhone cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    // 在类中定义锁 map
    private final Map<String, Object> wxLoginLockMap = new ConcurrentHashMap<>();

    @Transactional(rollbackFor = Exception.class)
    @Override
    public LoginUserVO userLoginByMpOpen(WxOAuth2UserInfo wxOAuth2UserInfo, HttpServletRequest request) {
        String unionId = wxOAuth2UserInfo.getUnionId();
        String mpOpenId = wxOAuth2UserInfo.getOpenid();

        if (StringUtils.isBlank(unionId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "微信用户信息为空");
        }

        // 加锁注册流程
        Object lock = wxLoginLockMap.computeIfAbsent(unionId, k -> new Object());
        synchronized (lock) {
            try {
                // 查询用户是否已存在
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("unionId", unionId);
                User user = this.getOne(queryWrapper);

                // 被封号，禁止登录
                if (user != null && UserRoleEnum.BAN.getValue().equals(user.getUserRole())) {
                    throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "该用户已被封，禁止登录");
                }

                // 用户不存在则创建
                if (user == null) {
                    user = new User();
                    user.setUnionId(unionId);
                    user.setMpOpenId(mpOpenId);
                    user.setUserAvatar(wxOAuth2UserInfo.getHeadImgUrl());
                    user.setUserName(wxOAuth2UserInfo.getNickname());

                    boolean result = this.save(user);
                    if (!result) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，用户创建失败");
                    }
                }

                // 记录用户的登录态
                request.getSession().setAttribute(USER_LOGIN_STATE, user);
                return getLoginUserVO(user);

            } finally {
                wxLoginLockMap.remove(unionId);
            }
        }
    }


    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        return this.getById(userId);
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        if (user.getBirthday() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(user.getBirthday());
            int birthYear = calendar.get(Calendar.YEAR);
            userVO.setBirthdayYear(birthYear);
        } else {
            userVO.setBirthdayYear(0); // 或者按需设为默认值
        }
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public UserVO getUserVOById(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = this.getById(userId);
        return getUserVO(user);
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        Integer userGender = userQueryRequest.getUserGender();
        String userPhone = userQueryRequest.getUserPhone();
        String userName = userQueryRequest.getUserName();
        String userRole = userQueryRequest.getUserRole();
        String userEmail = userQueryRequest.getUserEmail();
        String wechatId = userQueryRequest.getWechatId();
        int birthdayYear = userQueryRequest.getBirthdayYear();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.eq(StringUtils.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.eq(userGender != null, "userGender", userGender);
        queryWrapper.eq(StringUtils.isNotBlank(userPhone), "userPhone", userPhone);
        queryWrapper.eq(StringUtils.isNotBlank(userEmail), "userEmail", userEmail);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StringUtils.isNotBlank(wechatId), "wechatId", wechatId);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.apply(birthdayYear > 0, "YEAR(birthday) = {0}", birthdayYear);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public List<User> getAllAdmins() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userRole", UserRoleEnum.ADMIN.getValue());
        queryWrapper.eq("isDelete", 0); // 未删除的用户
        return this.list(queryWrapper);
    }
}
