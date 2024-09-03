package com.comfort.hotel.controller;

import com.comfort.hotel.constants.Constants;
import com.comfort.hotel.domain.dto.UserLoginDTO;
import com.comfort.hotel.domain.entity.User;
import com.comfort.hotel.mapper.UserMapper;
import com.comfort.hotel.service.UserService;
import com.comfort.hotel.utils.StringTools;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * @Author: ayuan
 * @PS: 用户相关业务
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
     * 获取邮箱验证码
     * @param userLoginDTO
     *
     */
    @ApiOperation(value = "获取邮箱验证码")
    @PostMapping("/getVerifyCode")
    public void getVerifyCode(@RequestBody UserLoginDTO userLoginDTO, HttpServletResponse response)  {
        // 如果不能正常返回
        if (!userService.getVerifyCode(userLoginDTO)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
     }

    /**
     * 验证用户
     * @param userLoginDTO
     * @param response
     */
    @ApiOperation(value = "验证用户")
    @PostMapping("/verify")
    public void verify(@RequestBody UserLoginDTO userLoginDTO, HttpServletResponse response) {
        if (!userService.verify(userLoginDTO)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
     }

    /**
     * 重置密码
     * @param userLoginDTO
     * @param response
     */
     @ApiOperation(value = "重置密码")
     @PostMapping("/resetPwd")
     public void resetPwd(@RequestBody UserLoginDTO userLoginDTO, HttpServletResponse response) {
         if (!userService.resetPwd(userLoginDTO)) {
             response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         }
     }

    /**
     * 用户注册
     * @param userLoginDTO
     */
    @ApiOperation(value = "用户注册")
    @PostMapping("/signIn")
    public void signIn(HttpSession session, @RequestBody UserLoginDTO userLoginDTO) {
        User user = new User();
        user.setUserId(StringTools.getRandomString(Constants.LENGTH_8));
        BeanUtils.copyProperties(userLoginDTO,user);
        userService.signInUser(user);
    }

    /**
     * 用户登录
     * @param session
     * @param response
     * @param userLoginDTO
     */
    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public void login(HttpSession session, HttpServletResponse response, @RequestBody UserLoginDTO userLoginDTO){
        User user = new User();
        BeanUtils.copyProperties(userLoginDTO,user);
        // 查找数据库，查看用户是否已经注册,获取密码匹配错误的
        String userId = userService.loginUserAndGetId(user);
        if (userId != null) {
            // 不为空，说明当前用户已经注册，可以为当前用户设置token
            String token =  UUID.randomUUID().toString();
            // 将token存储在redis中，并且设置过期时间为30分钟
            redisTemplate.opsForValue().set(Constants.USER_ID + userId, token, Constants.THIRTY, TimeUnit.MINUTES);
            // 将 userId和token分别存储到session中， 方便以后使用
            session.setAttribute(Constants.USER_ID, userId);
            session.setAttribute(Constants.TOKEN, token);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
