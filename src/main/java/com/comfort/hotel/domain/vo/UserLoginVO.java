package com.comfort.hotel.domain.vo;

import lombok.Data;

/**
 * @Author: ayuan
 * @PS: 用户登录交互实体
 * @Date: 2024/8/29 11:34
 */
@Data
public class UserLoginVO {

    // 账号
    private String email;

    // 密码
    private String password;

    //用户状态 -> 0：禁用, 1: 正常使用
    private String userStatus;

    // 用户重置密码验证码
    private String verifyCode;
}
