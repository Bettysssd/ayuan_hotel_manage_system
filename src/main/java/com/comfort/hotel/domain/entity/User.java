package com.comfort.hotel.domain.entity;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 【请填写功能名称】对象 user
 *
 * @author ayuan
 * @date 2024-08-29
 */
@Data
public class User {
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private String userId;

    /** 用户头像 */
    private String userCover;

    /** 用户姓名 */
    private String userName;

    /** 用户昵称 */
    private String nickName;

    /** 用户账号，使用邮箱注册 */
    private String qqEmail;

    /** 密码,第一次赋初始化，稍后修改 */
    private String password;

    /** 性别-> 0: 男， 1: 女 */
    private Long gender;

    /** 用户注册时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date signInTime;

    /** 最近一次登录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date lastLoginTime;

    /** 身份证号码 */
    private String idCardCode;

    /** 联系电话 */
    private String phoneNumber;

    /** 消费金额 */
    private Long spendAmount;

    /** 微信账号 */
    private String wechatAccount;

    /** 房间号ID */
    private String roomId;

    /** 汽车号牌 */
    private String parkingId;

    /** 服务员ID */
    private String waiterId;

    /** 食堂ID */
    private String canteenId;

    /** 用户状态 */
    private Integer userStatus;



}
