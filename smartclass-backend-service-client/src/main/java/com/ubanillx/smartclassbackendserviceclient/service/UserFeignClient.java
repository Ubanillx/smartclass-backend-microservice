package com.ubanillx.smartclassbackendserviceclient.service;

import com.ubanillx.smartclassbackendcommon.common.ErrorCode;
import com.ubanillx.smartclassbackendcommon.exception.BusinessException;
import com.ubanillx.smartclassbackendmodel.model.entity.User;
import com.ubanillx.smartclassbackendmodel.model.enums.UserRoleEnum;
import com.ubanillx.smartclassbackendmodel.model.vo.LoginUserVO;
import com.ubanillx.smartclassbackendmodel.model.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import static com.ubanillx.smartclassbackendcommon.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务
*/
@FeignClient(name = "smartclass-user-service", path = "/api/user/inner")
public interface UserFeignClient {

    /**
     * 获取当前登录用户
     * 简单方法，使用default实现，不走OpenFeign
     *
     * @param request
     * @return
     */
    default User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 需要从远程服务获取最新的用户信息
        User latestUser = this.getById(currentUser.getId());
        if (latestUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return latestUser;
    }

    /**
     * 获取当前登录用户（允许未登录）
     * 简单方法，使用default实现，不走OpenFeign
     *
     * @param request
     * @return
     */
    default User getLoginUserPermitNull(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }
        // 从远程服务查询最新数据
        return this.getById(currentUser.getId());
    }

    /**
     * 是否为管理员
     * 简单方法，使用default实现，不走OpenFeign
     *
     * @param user
     * @return
     */
    default boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 获取脱敏的已登录用户信息
     * 简单方法，使用default实现，不走OpenFeign
     *
     * @return
     */
    default LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    /**
     * 获取脱敏的用户信息
     * 简单方法，使用default实现，不走OpenFeign
     *
     * @param user
     * @return
     */
    default UserVO getUserVO(User user) {
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
            userVO.setBirthdayYear(0);
        }
        return userVO;
    }

    /**
     * 根据ID获取用户信息
     * 远程调用方法
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/get/{id}")
    User getById(@PathVariable("id") Long id);

    /**
     * 获取脱敏的用户信息列表
     * 复杂方法，需要批量处理，通过OpenFeign调用
     *
     * @param userList 用户列表
     * @return 脱敏后的用户信息列表
     */
    @PostMapping("/getUserVOList")
    List<UserVO> getUserVO(@RequestBody List<User> userList);
    
    /**
     * 根据用户ID获取脱敏的用户信息
     * 复杂方法，需要数据库查询，通过OpenFeign调用
     *
     * @param userId 用户ID
     * @return 脱敏后的用户信息
     */
    @GetMapping("/getUserVO/{userId}")
    UserVO getUserVOById(@PathVariable("userId") Long userId);

    /**
     * 根据用户ID集合批量获取用户信息
     * 复杂方法，需要数据库查询，通过OpenFeign调用
     *
     * @param userIds 用户ID集合
     * @return 用户信息列表
     */
    @PostMapping("/listByIds")
    List<User> listByIds(@RequestBody Collection<Long> userIds);

    /**
     *  更新用户信息
     * 复杂方法，需要数据库查询，通过OpenFeign调用
     *
     * @param user 用户
     * @return 是否成功
     */
    @PostMapping("/updateById")
    boolean updateById(User user);

}
