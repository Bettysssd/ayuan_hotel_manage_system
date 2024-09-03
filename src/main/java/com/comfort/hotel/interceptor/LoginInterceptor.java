package com.comfort.hotel.interceptor;

import com.comfort.hotel.constants.Constants;


import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * @Author: ayuan
 * @PS: 登录拦截器，获取token值，并进行校验
 * @Date: 2024/8/30 12:21
 */

@Component
public class LoginInterceptor implements HandlerInterceptor {


    @Resource
    RedisTemplate<String,String> redisTemplate;



    @Override
    //Controller逻辑执行之前
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 放行静态资源和注册以及登录的请求
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        // 获取登录后的session
        String token = (String)  request.getSession().getAttribute(Constants.TOKEN);
        String cachedToken = redisTemplate.opsForValue().get(Constants.USER_ID);
        if (token == null || cachedToken != null && !cachedToken.equals(token)) {
            // 未登录，返回 false 阻止请求继续处理
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 设置状态码为 401 Unauthorized
            return false;
        }
      return true;
    }

    //Controller逻辑执行完毕但是视图解析器还未进行解析之前
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        System.out.println("postHandle....");
    }

    //Controller逻辑和视图解析器执行完毕
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        System.out.println("afterCompletion....");
    }
}