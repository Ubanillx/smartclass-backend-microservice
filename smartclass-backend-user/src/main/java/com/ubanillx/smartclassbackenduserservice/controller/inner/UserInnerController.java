package com.ubanillx.smartclassbackenduserservice.controller.inner;

import com.ubanillx.smartclassbackendmodel.model.entity.User;
import com.ubanillx.smartclassbackendmodel.model.vo.UserVO;
import com.ubanillx.smartclassbackendserviceclient.service.UserFeignClient;
import com.ubanillx.smartclassbackenduserservice.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/inner")
public class UserInnerController implements UserFeignClient {

    @Resource
    private UserService userService;
    /**
     * 根据ID获取用户信息
     * 远程调用方法
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @Override
    @GetMapping("/get/{id}")
    public User getById(@PathVariable("id") Long id){
        return userService.getById(id);
    }


    /**
     * 获取脱敏的用户信息列表
     * 复杂方法，需要批量处理，通过OpenFeign调用
     *
     * @param userList 用户列表
     * @return 脱敏后的用户信息列表
     */
    @Override
    @PostMapping("/getUserVOList")
    public List<UserVO> getUserVO(@RequestBody List<User> userList){
        return userService.getUserVO(userList);
    }

    /**
     * 根据用户ID获取脱敏的用户信息
     * 复杂方法，需要数据库查询，通过OpenFeign调用
     *
     * @param userId 用户ID
     * @return 脱敏后的用户信息
     */
    @Override
    @GetMapping("/getUserVO/{userId}")
    public UserVO getUserVOById(@PathVariable("userId") Long userId){
        return userService.getUserVOById(userId);
    }

    @Override
    @PostMapping("/listByIds")
    public List<User> listByIds(@RequestBody Collection<Long> userIds){
        return userService.listByIds(userIds);
    }

    /**
     *  更新用户信息
     * 复杂方法，需要数据库查询，通过OpenFeign调用
     *
     * @param user 用户
     * @return 是否成功
     */
    @Override
    @PostMapping("/updateById")
    public boolean updateById(@RequestBody User user){
        return userService.updateById(user);
    }


}
