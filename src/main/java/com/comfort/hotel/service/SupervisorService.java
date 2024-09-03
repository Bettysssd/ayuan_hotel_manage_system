package com.comfort.hotel.service;


import com.comfort.hotel.domain.entity.Supervisor;


/**
 * @Author: ayuan
 * @PS:
 * @Date: 2024/9/2 15:27
 */
public interface SupervisorService {

    // 管理员登录
    String loginSuperVisorAndGetId(Supervisor supervisor);


}
