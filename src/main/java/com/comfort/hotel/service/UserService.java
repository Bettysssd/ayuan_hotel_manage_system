package com.comfort.hotel.service;

import com.comfort.hotel.domain.dto.UserLoginDTO;
import com.comfort.hotel.domain.dto.UserPayDTO;
import com.comfort.hotel.domain.entity.User;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @Author: ayuan
 * @PS: 用户管理接口
 * @Date: 2024/8/29 17:47
 */
public interface UserService {

    // 用户注册
    void signInUser(User user);

    // 获取邮箱验证码
    boolean getVerifyCode(UserLoginDTO userLoginDTO);

    // 验证是否为当前用户操作
    boolean verify(UserLoginDTO userLoginDTO);

    // 为用户重置密码
    boolean resetPwd(UserLoginDTO userLoginDTO);

    // 用户登录
    void login(HttpServletResponse response, UserLoginDTO userLoginDTO);

    // 用户登出
    void logout();

    // 通过用户特征分类用户
    Integer distinguishUserByCondition(Integer gender, Integer status);

    // 异步获取不同状态下的用户消费情况
    List<UserPayDTO> getUserDifferentPaySituation(Integer status);
}
