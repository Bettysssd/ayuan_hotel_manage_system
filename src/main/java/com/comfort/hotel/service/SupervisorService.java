package com.comfort.hotel.service;


import com.comfort.hotel.domain.dto.PageDTO;
import com.comfort.hotel.domain.dto.RoomDTO;
import com.comfort.hotel.domain.dto.SuperVisorLoginDTO;
import com.comfort.hotel.domain.dto.UserDTO;
import com.comfort.hotel.domain.entity.Supervisor;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * @Author: ayuan
 * @PS: 管理员接口
 * @Date: 2024/9/2 15:27
 */
public interface SupervisorService {


    // 管理员登录
    void login(HttpServletResponse response, SuperVisorLoginDTO superVisorLoginDTO);

    // 管理员登出
    void logout();

    // 分页查询用户信息
    PageInfo<UserDTO> findUserInList(PageDTO pageDTO);

    // 修改用户当前状态
    void changeUserStatus(UserDTO userDTO);

    // 恢复默认用户头像
    void recoverDefaultImage(String imgUrl, String userId);


}
