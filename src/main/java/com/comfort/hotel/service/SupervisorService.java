package com.comfort.hotel.service;


import com.comfort.hotel.domain.vo.PageVO;
import com.comfort.hotel.domain.vo.SuperVisorLoginVO;
import com.comfort.hotel.domain.vo.UserVO;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletResponse;


/**
 * @Author: ayuan
 * @PS: 管理员接口
 * @Date: 2024/9/2 15:27
 */
public interface SupervisorService {


    // 管理员登录
    void login(HttpServletResponse response, SuperVisorLoginVO superVisorLoginVO);

    // 管理员登出
    void logout();

    // 分页查询用户信息
    PageInfo<UserVO> findUserInList(PageVO pageVO);

    // 修改用户当前状态
    void changeUserStatus(UserVO userVO);

    // 恢复默认用户头像
    void recoverDefaultImage(String imgUrl, String userId);


}
