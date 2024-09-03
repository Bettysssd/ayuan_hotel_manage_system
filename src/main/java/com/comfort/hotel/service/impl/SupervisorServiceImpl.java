package com.comfort.hotel.service.impl;

import com.comfort.hotel.domain.entity.Supervisor;
import com.comfort.hotel.mapper.SupervisorMapper;
import com.comfort.hotel.service.SupervisorService;
import com.comfort.hotel.utils.StringTools;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: ayuan
 * @PS:
 * @Date: 2024/9/2 16:12
 */
@Service
public class SupervisorServiceImpl implements SupervisorService {

    @Resource
    private SupervisorMapper supervisorMapper;

    // 管理员登录
    @Override
    public String loginSuperVisorAndGetId(Supervisor supervisor) {
        // 获取数据库密码
        String password = supervisorMapper.getSupervisorPasswordByAccount(supervisor.getAccount());
        // 判断登录密码是否一致
        if (password.equals(StringTools.encodeByMD5(supervisor.getPassword()))) {
            return supervisorMapper.getSupervisorIdByAccount(supervisor.getAccount());
        } else {
            return null;
        }
    }
}
