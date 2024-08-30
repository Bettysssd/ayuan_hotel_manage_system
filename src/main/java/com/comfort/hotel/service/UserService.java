package com.comfort.hotel.service;

import com.comfort.hotel.domain.entity.User;

import javax.servlet.http.HttpSession;

/**
 * @Author: ayuan
 * @PS:
 * @Date: 2024/8/29 17:47
 */
public interface UserService {

    // 用户注册
    void signInUser(User user);

    // 用户登录
    String loginUser(User user);
}
