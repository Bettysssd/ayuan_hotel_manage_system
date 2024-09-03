package com.comfort.hotel.service;

import com.comfort.hotel.domain.dto.UserLoginDTO;
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

    // 用户登录，并获取id
    String loginUserAndGetId(User user);

    // 获取邮箱验证码
    boolean getVerifyCode(UserLoginDTO userLoginDTO);

    // 验证是否为当前用户操作
    boolean verify(UserLoginDTO userLoginDTO);

    // 为用户重置密码
    boolean resetPwd(UserLoginDTO userLoginDTO);
}
