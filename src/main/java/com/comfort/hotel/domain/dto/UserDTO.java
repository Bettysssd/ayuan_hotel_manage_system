package com.comfort.hotel.domain.dto;

import lombok.Data;


/**
 * @Author: ayuan
 * @PS: 用户列表查询返回体
 * @Date: 2024/9/5 15:14
 */
@Data
public class UserDTO {

    private String userId;
    private String userCover;
    private String userName;
    private String email;
    private Integer gender;
    private String signInTime;
    private String lastLoginTime;
    private String idCardCode;
    private String phoneNumber;
    private Integer spendAmount;
    private String wechatAccount;
    private String roomMark;
    private Integer userStatus;
    private Integer payStatus;


}
