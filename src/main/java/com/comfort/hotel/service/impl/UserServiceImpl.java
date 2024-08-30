package com.comfort.hotel.service.impl;

import com.comfort.hotel.constants.Constants;
import com.comfort.hotel.domain.entity.User;
import com.comfort.hotel.mapper.UserMapper;
import com.comfort.hotel.service.UserService;
import com.comfort.hotel.utils.StringTools;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @Author: ayuan
 * @PS:
 * @Date: 2024/8/29 17:48
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 用户注册
     * @param user
     */
    @Override
    public void signInUser(User user) {
        // 密码加密
        user.setPassword(StringTools.encodeByMD5(user.getPassword()));
        userMapper.signInUser(user.getUserId(), user.getQqEmail(), user.getPassword());
    }

    /**
     * 用户登录
     * @param user
     */
    @Override
    public String loginUser(User user) {
        //数据库中，查询用户的id
        String userId = userMapper.selectUserIdByQQEmail(user.getQqEmail());
        if (userId != null) {
            return userId;
        }
        // 找不到，用户不存在返回空
        return null;
    }


}
