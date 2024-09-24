package com.comfort.hotel.controller;

import com.comfort.hotel.constants.Constants;
import com.comfort.hotel.domain.dto.UserLoginDTO;
import com.comfort.hotel.domain.dto.UserPayDTO;
import com.comfort.hotel.domain.entity.User;
import com.comfort.hotel.domain.vo.ResponseVO;
import com.comfort.hotel.service.UserService;
import com.comfort.hotel.utils.StringTools;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * @Author: ayuan
 * @PS: 用户相关业务
 * @Date: 2024/8/29 11:29
 */
@Api(tags = "用户相关业务")
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 异步获取不同状态下的用户消费情况
     * @param status
     * @return
     */
    @ApiOperation(value = "异步获取不同状态下的用户消费情况")
    @GetMapping("/getUserDifferentPaySituation")
    public ResponseVO<List<UserPayDTO>> getUserDifferentPaySituation(@RequestParam("status") Integer status) {
       return ResponseVO.success(userService.getUserDifferentPaySituation(status));
    }

    /**
     * 通过用户特征分类用户
     * @param type
     * @param status
     * @return
     */
    @ApiOperation(value = "通过用户特征分类用户")
    @GetMapping("/distinguishUserByCondition")
    public ResponseVO<Integer> distinguishUserByCondition(@RequestParam("type") Integer type,
                                                       @RequestParam("status") Integer status) {
        return ResponseVO.success(userService.distinguishUserByCondition(type, status));
    }

    /**
     * 用户登出
     */
    @ApiOperation(value = "用户登出")
    @GetMapping("/logout")
    public void logout() {
        userService.logout();
    }

    /**
     * 获取邮箱验证码
     *
     * @param userLoginDTO
     */
    @ApiOperation(value = "获取邮箱验证码")
    @PostMapping("/getVerifyCode")
    public void getVerifyCode(@RequestBody UserLoginDTO userLoginDTO, HttpServletResponse response) {
        // 如果不能正常返回
        if (!userService.getVerifyCode(userLoginDTO)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * 验证用户
     *
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
     *
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
     *
     * @param userLoginDTO
     */
    @ApiOperation(value = "用户注册")
    @PostMapping("/signIn")
    public void signIn(@RequestBody UserLoginDTO userLoginDTO) {
        User user = new User();
        user.setUserId(StringTools.getRandomString(Constants.LENGTH_8));
        BeanUtils.copyProperties(userLoginDTO, user);
        userService.signInUser(user);
    }

    /**
     * 用户登录
     *
     * @param response
     * @param userLoginDTO
     */
    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public void login(HttpServletResponse response, @RequestBody UserLoginDTO userLoginDTO) {
        userService.login(response, userLoginDTO);
    }
}
