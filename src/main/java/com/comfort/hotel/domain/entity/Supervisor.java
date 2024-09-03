package com.comfort.hotel.domain.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: ayuan
 * @PS: 管理员实体
 * @Date: 2024/9/2 15:33
 */
@Data
public class Supervisor implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 管理员ID */
    private String supervisorId;

    /** 管理员昵称 */
    private String supervisorName;

    /** 管理员账号 */
    private String account;

    /** 管理员密码 */
    private String password;
}
