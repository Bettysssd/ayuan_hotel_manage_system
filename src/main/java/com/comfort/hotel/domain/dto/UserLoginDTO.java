package com.comfort.hotel.domain.dto;

import lombok.Data;

/**
 * @Author: ayuan
 * @PS: 用户登录交互
 * @Date: 2024/8/29 11:34
 */
@Data
public class UserLoginDTO {

    // 账号
    private String qqEmail;

    // 密码
    private String password;

    //用户状态 -> 0：禁用, 1: 正常使用
    private String userStatus;
}
