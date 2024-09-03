package com.comfort.hotel.controller;

import com.comfort.hotel.constants.Constants;
import com.comfort.hotel.domain.dto.SuperVisorLoginDTO;
import com.comfort.hotel.domain.entity.Supervisor;
import com.comfort.hotel.service.SupervisorService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author: ayuan
 * @PS: 管理员相关业务
 * @Date: 2024/9/2 15:22
 */
@RestController
@RequestMapping("/supervisor")
public class SupervisorController {

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private SupervisorService supervisorService;



    /**
     * 管理员登录
     * @param session
     * @param response
     * @param superVisorLoginDTO
     */
    @ApiOperation(value = "管理员登录")
    @PostMapping("/login")
    public void login(HttpSession session, HttpServletResponse response, @RequestBody SuperVisorLoginDTO superVisorLoginDTO){
        Supervisor supervisor = new Supervisor();
        BeanUtils.copyProperties(superVisorLoginDTO,supervisor);
        // 查找数据库，查看用户是否已经注册,获取密码匹配错误的
        String supervisorId = supervisorService.loginSuperVisorAndGetId(supervisor);
        if (supervisorId != null) {
            String token =  UUID.randomUUID().toString();
            // 将token存储在redis中，并且设置过期时间为30分钟
            redisTemplate.opsForValue().set(Constants.SUPERVISOR_ID + supervisorId, token, 30, TimeUnit.MINUTES);
            // 将 userId和token分别存储到session中， 方便以后使用
            session.setAttribute(Constants.SUPERVISOR_ID, supervisorId);
            session.setAttribute(Constants.TOKEN, token);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
