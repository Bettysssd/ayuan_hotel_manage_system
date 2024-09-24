package com.comfort.hotel.controller;

import com.comfort.hotel.domain.dto.PageDTO;
import com.comfort.hotel.domain.dto.SuperVisorLoginDTO;
import com.comfort.hotel.domain.dto.UserDTO;
import com.comfort.hotel.domain.vo.ResponseVO;
import com.comfort.hotel.service.SupervisorService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;



/**
 * @Author: ayuan
 * @PS: 管理员相关业务
 * @Date: 2024/9/2 15:22
 */
@Api(tags = "管理员相关业务")
@RestController
@RequestMapping("/supervisor")
public class SupervisorController {

   @Resource
   private SupervisorService supervisorService;


    /**
     * 分页查询用户信息
     * @param pageDTO
     * @return
     */
    @ApiOperation(value = "分页查询用户信息")
    @PostMapping("/queryUserList")
    public ResponseVO<PageInfo<UserDTO>> showUserList(@RequestBody PageDTO pageDTO){
        PageInfo<UserDTO> userInList = supervisorService.findUserInList(pageDTO);
        return ResponseVO.success(userInList);
    }

    /**
     * 修改用户状态
     * @param userDTO
     */
    @ApiOperation(value = "修改用户状态")
    @PostMapping("/changeUserStatus")
    public void changeUserStatus( @RequestBody UserDTO userDTO){
        supervisorService.changeUserStatus(userDTO);
    }

    /**
     * 恢复默认用户头像
     * @param imgUrl
     */
    @ApiOperation(value = "恢复默认用户头像")
    @GetMapping("/recoverDefaultImage")
    public void recoverDefaultImage(@RequestParam String imgUrl, @RequestParam String userId) {
        supervisorService.recoverDefaultImage(imgUrl, userId);
    }

    /**
     * 管理员登出
     * @paam session
     */
    @ApiOperation(value = "管理员登出")
    @GetMapping("/logout")
    public void logout() {
        supervisorService.logout();
    }

    /**
     * 管理员登录
     * @param response
     * @param superVisorLoginDTO
     */
    @ApiOperation(value = "管理员登录")
    @PostMapping("/login")
    public void login( HttpServletResponse response, @RequestBody SuperVisorLoginDTO superVisorLoginDTO){
        supervisorService.login(response, superVisorLoginDTO);
    }

}
