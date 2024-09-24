package com.comfort.hotel.interceptor;

import com.comfort.hotel.constants.Constants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;


/**
 * @Author: ayuan
 * @PS: 登录拦截器，获取token值，并进行校验
 * @Date: 2024/8/30 12:21
 */

@Component
public class LoginInterceptor implements HandlerInterceptor {


    @Resource
    RedisTemplate<String, String> redisTemplate;


    @Override
    //Controller逻辑执行之前
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 放行静态资源和注册以及登录的请求
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 获取登录后的session
        String token = request.getHeader("Authorization");
        // 如果当前token有效的是用户或管理员
        if (Objects.equals(redisTemplate.opsForValue().get(Constants.USER_TOKEN), token) ||
                Objects.equals(redisTemplate.opsForValue().get(Constants.SUPERVISOR_TOKEN), token)) {
            return true;
        }else {
            response.setHeader(Constants.NOTICE_INFO,"unable");
            return false;
        }
    }
}