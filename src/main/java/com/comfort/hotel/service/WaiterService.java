package com.comfort.hotel.service;

import com.comfort.hotel.domain.vo.PageVO;
import com.comfort.hotel.domain.vo.ReviewVO;
import com.comfort.hotel.domain.vo.WaiterVO;
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
    void updateWaiterInfo(WaiterVO waiterVO);

    // 删除当前管理员
    void deleteCurrentWaiter(String waiterId);

    // 录入管理员信息
    void addWaiterInfo(WaiterVO waiterVO);

    // 分页查询管理员信息
    PageInfo<WaiterVO> findWaiterInList(PageVO pageVO);

    // 根据工作类型查询不同的管理员人数
    Integer getWaiterCountByType(Integer[] types);

    // 根据状态查询不同状态的泊车员人数
    Integer getParkingByStatus(Integer integers);

    // 查看当前服务员信息
    ReviewVO showCurrentWaiterAndGoods(String waiterId, String objectId, Integer objectStatus);

    // 评价当前服务员和商品
    boolean reviewCurrentWaiterAndGoods(String waiterId, String objectId,
                                        String orderId, String userId,
                                        Integer objectStatus, Integer status);
}
