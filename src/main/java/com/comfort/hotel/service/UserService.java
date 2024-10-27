package com.comfort.hotel.service;

import com.comfort.hotel.domain.entity.User;
import com.comfort.hotel.domain.qo.ParkingQO;
import com.comfort.hotel.domain.vo.ParkingInfoVO;
import com.comfort.hotel.domain.vo.UserVO;
import com.comfort.hotel.domain.vo.UserLoginVO;
import com.comfort.hotel.domain.vo.UserPayVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author: ayuan
 * @PS: 用户管理接口
 * @Date: 2024/8/29 17:47
 */
public interface UserService {

    // 用户注册
    boolean signInUser(User user);

    // 获取邮箱验证码
    boolean getVerifyCode(UserLoginVO userLoginVO);

    // 验证是否为当前用户操作
    boolean verify(UserLoginVO userLoginVO);

    // 为用户重置密码
    boolean resetPwd(UserLoginVO userLoginVO);

    // 用户登录
    void login(HttpServletResponse response, UserLoginVO userLoginVO);

    // 用户登出
    void logout();

    // 通过用户特征分类用户
    Integer distinguishUserByCondition(Integer gender, Integer status);

    // 异步获取不同状态下的用户消费情况
    List<UserPayVO> getUserDifferentPaySituation(Integer status);

    // 获取用户头像
    String getUserAvatar(String userId);

    // 获取用户基本信息
    UserVO getUserInfo(String userId);

    // 更新用户图片
    void uploadUserCover(MultipartFile file);

    // 更新用户信息
    Integer updateUserInfo(UserVO userVO);

    // 查看用户是否已经填完必填的个人信息
    boolean checkUserInfoAlreadyFinished(String userId);

    // 酒店客房预定
    boolean bookHotel(String roomId, String userId, Integer count, Float price, String roomName);

    // 酒店客房合租
    Integer rentalTogether(String idCode, String userId, Integer count, String roomId);

    // 商品下单
    String bookGoods(String goodsId, String userId, Integer currentSelectGoodsAmount, Float price);

    // 餐馆下单
    String bookCanteen(String canteenId, String userId, Integer currentSelectedPersonAmount);

    // 泊车下单
    String bookParking(ParkingQO parkingQO);
}
