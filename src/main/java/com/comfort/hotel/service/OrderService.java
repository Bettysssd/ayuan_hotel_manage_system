package com.comfort.hotel.service;

import com.comfort.hotel.domain.vo.OrderVO;
import com.comfort.hotel.domain.vo.PageVO;
import com.comfort.hotel.domain.vo.ParkingInfoVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @Author: ayuan
 * @PS: 下单管理接口
 * @Date: 2024/10/12 11:09
 */


public interface OrderService {

    // 分页查询当前用户订单信息
    PageInfo<OrderVO> findOrdersInList(PageVO pageVO);

    // 删除用户订单信息
    void deleteCurrentOrderInfo(String userId, String orderId);

    // 支付当前订单
    void payCurrentOrder(String userId, String orderId, Float orderSpend);

    // 导出用户订单信息
    List<OrderVO> exportOrderList(String userId);

    // 取消当前订单
    boolean cancelCurrentOrder(String userId, String orderId, String objectId, Integer objectStatus, Integer orderAmount, boolean isAutoCancel);

    // 查询当前停车订单信息
    ParkingInfoVO searchCurrentParkingInfo(String userId);
}