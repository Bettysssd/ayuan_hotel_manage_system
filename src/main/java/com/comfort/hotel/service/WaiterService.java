package com.comfort.hotel.service;

import com.comfort.hotel.domain.dto.PageDTO;
import com.comfort.hotel.domain.dto.WaiterDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: ayuan
 * @PS: 管理员管理接口
 * @Date: 2024/9/14 17:48
 */

public interface WaiterService {

    // 更新管理员图片
    void uploadWaiterCover(MultipartFile file);

    // 更新管理员基本信息
    void updateWaiterInfo(WaiterDTO waiterDTO);

    // 删除当前管理员
    void deleteCurrentWaiter(String waiterId);

    // 录入管理员信息
    void addWaiterInfo(WaiterDTO waiterDTO);

    // 分页查询管理员信息
    PageInfo<WaiterDTO> findWaiterInList(PageDTO pageDTO);

    // 根据工作类型查询不同的管理员人数
    Integer getWaiterCountByType(Integer[] types);
}
