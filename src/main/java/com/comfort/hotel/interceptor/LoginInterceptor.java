package com.comfort.hotel.interceptor;

import com.comfort.hotel.constants.Constants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Author: ayuan
 * @PS: 拦截器，获取token值，并进行校验
 * @Date: 2024/8/30 12:21
 */


public class LoginInterceptor implements HandlerInterceptor {


    @Resource
    RedisTemplate<String, String> redisTemplate;


    //Controller逻辑执行之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);  // 不创建新会话，如果会话不存在则返回 null
        /**
         * HandlerMethod=>Controller中标注@RequestMapping的方法
         *  需要配置静态资源不拦截时，添加这块逻辑  => 前后端分离项目
         *
         */
        // 是我们的conrtoller中的方法
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String token = request.getHeader(Constants.TOKEN);
        String cachedToken = redisTemplate.opsForValue().get(Constants.USER_ID);
        if (cachedToken != null && !cachedToken.equals(token)) {
            // 未登录，返回 false 阻止请求继续处理
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 设置状态码为 401 Unauthorized
            response.getWriter().write("未登录或登录校验已过期！");  // 可以选择性地返回一些错误信息
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