package com.comfort.hotel.domain.vo;

import lombok.Data;

/**
 * @Author: ayuan
 * @PS: 管理员登录交互实体
 * @Date: 2024/9/2 15:28
 */
@Data
public class SuperVisorLoginVO {

    // 账号
    private String account;
    // 密码
    private String password;
}
