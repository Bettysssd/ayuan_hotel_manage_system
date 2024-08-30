package com.comfort.hotel.controller;

import com.comfort.hotel.constants.Constants;
import com.comfort.hotel.domain.dto.UserLoginDTO;
import com.comfort.hotel.domain.entity.User;
import com.comfort.hotel.service.UserService;
import com.comfort.hotel.utils.StringTools;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * @Author: ayuan
 * @PS:
 * @Date: 2024/8/29 11:29
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 用户注册
     * @param userLoginDTO
     */
    @ApiOperation(value = "用户注册")
    @PostMapping("/signIn")
    public void signIn(@RequestBody UserLoginDTO userLoginDTO) {
        User user = new User();
        user.setUserId(StringTools.getRandomString(Constants.LENGTH_8));
        BeanUtils.copyProperties(userLoginDTO,user);
        userService.signInUser(user);
    }


    /**
     * 用户登录
     * @param userLoginDTO
     */
    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public void login(HttpSession session, HttpServletResponse response, @RequestBody UserLoginDTO userLoginDTO) throws IOException {
        User user = new User();
        BeanUtils.copyProperties(userLoginDTO,user);
        // 查找数据库，查看用户是否已经注册
        String userId = userService.loginUser(user);
        if (userId != null) {
            // 不为空，说明当前用户已经注册，可以为当前用户设置token
            String token =  UUID.randomUUID().toString();
            // 将token存储在redis中，并且设置过期时间为30分钟
            redisTemplate.opsForValue().set(userId, token, 30, TimeUnit.MINUTES);
            // 将 userId存储到session中， 方便以后使用
            session.setAttribute(Constants.USER_ID, userId);
            response.getWriter().write("登录成功！");
        }
    }
}
