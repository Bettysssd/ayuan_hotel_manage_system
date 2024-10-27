package com.comfort.hotel.controller;

import com.comfort.hotel.constants.Constants;
import com.comfort.hotel.domain.entity.User;
import com.comfort.hotel.domain.qo.ParkingQO;
import com.comfort.hotel.domain.vo.ParkingInfoVO;
import com.comfort.hotel.domain.vo.ResponseVO;
import com.comfort.hotel.domain.vo.UserVO;
import com.comfort.hotel.domain.vo.UserLoginVO;
import com.comfort.hotel.domain.vo.UserPayVO;
import com.comfort.hotel.service.UserService;
import com.comfort.hotel.utils.StringTools;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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
     * 泊车下单
     * @param parkingQO
     * @return
     */
    @ApiOperation(value = "泊车下单")
    @PostMapping("/bookParking")
    public ResponseVO bookParking(@Valid @RequestBody ParkingQO parkingQO) {
        String result = userService.bookParking(parkingQO);
        if (result.contains("1")) {
            return ResponseVO.success();
        } else {
            return ResponseVO.error(result);
        }
    }



    @ApiOperation(value = "餐馆下单")
    @GetMapping("/bookCanteen")
    public ResponseVO bookCanteen(@RequestParam("canteenId") String canteenId, @RequestParam("userId") String userId,
                               @RequestParam("currentSelectedPersonAmount") Integer currentSelectedPersonAmount) {
        String result = userService.bookCanteen(canteenId, userId, currentSelectedPersonAmount);
        if (result.contains("1")) {
            return ResponseVO.success("当前餐厅预定成功，请在150分钟完毕用餐,并支付订单！");
        } else {
            return ResponseVO.error(result);
        }
    }



    @ApiOperation(value = "商品下单")
    @GetMapping("/bookGoods")
    public ResponseVO bookGoods(@RequestParam("goodsId") String goodsId, @RequestParam("userId") String userId,
            @RequestParam("currentSelectedGoodsAmount") Integer currentSelectedGoodsAmount, @RequestParam("price") Float price) {
        String result = userService.bookGoods(goodsId, userId, currentSelectedGoodsAmount, price);
        if (result.contains("success")) {
            return ResponseVO.success("当前商品下单成功，请在15分钟内支付！");
        } else {
            return ResponseVO.error(result);
        }
    }


    /**
     * 酒店客房合租
     * @return
     */
    @ApiOperation(value = "酒店客房合租")
    @GetMapping("/rentalTogether")
    public ResponseVO rentalTogether(@Param("idCode") String idCode, @Param("userId") String userId,
                                     @Param("currentMarkPeople") Integer currentMarkPeople, @Param("roomId") String roomId) {
       Integer status = userService.rentalTogether(idCode, userId, currentMarkPeople, roomId);
       switch (status) {
           case 0: return ResponseVO.errorWithStatus("当前用户已经预定或合租过酒店了，不能再进行合租操作", 2);
           case 1: return ResponseVO.errorWithStatus("当前合租人没有预定客房或当前合租人不存在",3);
           case 2: return ResponseVO.errorWithStatus("当前合租人预定的房间与您选择合租的房间不一致",4);
           case 4: return ResponseVO.errorWithStatus("当前合租人还没有支付成功，请稍等或联系他",5);
       }
        return ResponseVO.success();
    }

    /**
     * 酒店客房预定
     * @param roomId
     * @param userId
     * @return
     */
    @ApiOperation(value = "酒店客房预定")
    @GetMapping("/bookHotel")
    public ResponseVO bookHotel(@RequestParam("roomId") String roomId, @RequestParam("userId") String userId,
                                @RequestParam("currentMarkPeople") Integer currentMarkPeople,
                                @RequestParam("price") Float price, @RequestParam("roomName") String roomName) {
        if (userService.bookHotel(roomId, userId, currentMarkPeople, price, roomName)) {
            return ResponseVO.success();
        } else {
            return ResponseVO.error("您已经预定过酒店客房了，不可重复预定！");
        }
    }


    /**
     * 查看用户是否已经填完必填的个人信息
     * @param userId
     * @return
     */
    @ApiOperation(value = "查看用户是否已经填完必填的个人信息")
    @GetMapping("/checkUserInfoAlreadyFinished")
    public ResponseVO checkUserInfoAlreadyFinished(@Param("userId") String userId) {
        boolean isAlreadyFinished = userService.checkUserInfoAlreadyFinished(userId);
        if (isAlreadyFinished) {
            return ResponseVO.success();
        } else {
            return ResponseVO.error("请点击头像，完成个人信息的填写再继续操作！");
        }
    }

    /**
     * 更新用户信息
     * @param userVO
     */
    @ApiOperation(value = "更新用户信息")
    @PostMapping("/updateUserInfo")
    public ResponseVO updateUserInfo(@RequestBody UserVO userVO) {
        switch (userService.updateUserInfo(userVO)) {
            case 1: return ResponseVO.error("当前身份证号码已经登记过了！");
            case 2: return ResponseVO.error("当前手机号码已经登记过了！");
            case 3: return ResponseVO.error("当前微信号码已经登记过了！");
        }
       return ResponseVO.success();
    }

    /**
     * 更新用户图片
     * @param file
     */
    @ApiOperation(value = "更新用户图片")
    @PostMapping("/uploadUserCover")
    public void uploadUserCover(@RequestParam("file") MultipartFile file) {
        userService.uploadUserCover(file);
    }

    /**
     * 获取用户基本信息
     * @param userId
     * @return
     */
    @ApiOperation(value = "获取用户基本信息")
    @GetMapping("/getUserInfo")
    public ResponseVO<UserVO> getUserInfo(@RequestParam("userId") String userId) {
       return ResponseVO.success(userService.getUserInfo(userId));
    }


    /**
     * 获取用户头像
     * @param userId
     * @return
     */
    @ApiOperation(value = "获取用户头像")
    @GetMapping("/getUserAvatar")
    public ResponseVO<String> getUserAvatar(@RequestParam("userId") String userId) {
        return ResponseVO.success(userService.getUserAvatar(userId));
    }

    /**
     * 异步获取不同状态下的用户消费情况
     * @param status
     * @return
     */
    @ApiOperation(value = "异步获取不同状态下的用户消费情况")
    @GetMapping("/getUserDifferentPaySituation")
    public ResponseVO<List<UserPayVO>> getUserDifferentPaySituation(@RequestParam("status") Integer status) {
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
     * @param userLoginVO
     */
    @ApiOperation(value = "获取邮箱验证码")
    @PostMapping("/getVerifyCode")
    public void getVerifyCode(@RequestBody UserLoginVO userLoginVO, HttpServletResponse response) {
        // 如果不能正常返回
        if (!userService.getVerifyCode(userLoginVO)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * 验证用户
     *
     * @param userLoginVO
     * @param response
     */
    @ApiOperation(value = "验证用户")
    @PostMapping("/verify")
    public void verify(@RequestBody UserLoginVO userLoginVO, HttpServletResponse response) {
        if (!userService.verify(userLoginVO)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    /**
     * 重置密码
     *
     * @param userLoginVO
     * @param response
     */
    @ApiOperation(value = "重置密码")
    @PostMapping("/resetPwd")
    public void resetPwd(@RequestBody UserLoginVO userLoginVO, HttpServletResponse response) {
        if (!userService.resetPwd(userLoginVO)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * 用户注册
     *
     * @param userLoginVO
     */
    @ApiOperation(value = "用户注册")
    @PostMapping("/signIn")
    public ResponseVO signIn(@RequestBody UserLoginVO userLoginVO) {
        User user = new User();
        user.setUserId(StringTools.getRandomString(Constants.LENGTH_8));
        BeanUtils.copyProperties(userLoginVO, user);
        if (userService.signInUser(user)) {
            return ResponseVO.success();
        } else {
            return ResponseVO.error("当前邮箱账号已经注册过了！");
        }
    }

    /**
     * 用户登录
     *
     * @param response
     * @param userLoginVO
     */
    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public void login(HttpServletResponse response, @RequestBody UserLoginVO userLoginVO) {
        userService.login(response, userLoginVO);
    }
}
