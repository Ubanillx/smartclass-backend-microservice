package com.ubanillx.smartclassbackendmodel.model.dto.user;

import com.ubanillx.smartclassbackendcommon.common.PageRequest;import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户查询请求
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;


    /**
     * 用户性别 0-男 1-女 2-保密
     */
    private Integer userGender;

    /**
     * 手机号
     */
    private String userPhone;

    /**
     * 用户昵称
     */
    private String userName;


    /**
     * 用户角色：student/teacher/admin/ban
     */
    private String userRole;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 用户微信号
     */
    private String wechatId;

    /**
     * 用户生日
     */
    private int birthdayYear;


    private static final long serialVersionUID = 1L;
}