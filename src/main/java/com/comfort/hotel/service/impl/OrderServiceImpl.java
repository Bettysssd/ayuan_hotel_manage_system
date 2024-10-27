package com.comfort.hotel.service.impl;

import cn.hutool.core.date.DateTime;
import com.comfort.hotel.constants.Constants;
import com.comfort.hotel.domain.vo.CanteenVO;
import com.comfort.hotel.domain.vo.CarVO;
import com.comfort.hotel.domain.vo.GoodsVO;
import com.comfort.hotel.domain.vo.OrderVO;
import com.comfort.hotel.domain.vo.PageVO;
import com.comfort.hotel.domain.vo.ParkingInfoVO;
import com.comfort.hotel.domain.vo.RoomVO;
import com.comfort.hotel.domain.vo.UserVO;
import com.comfort.hotel.domain.vo.WaiterVO;
import com.comfort.hotel.mapper.CanteenMapper;
import com.comfort.hotel.mapper.GoodsMapper;
import com.comfort.hotel.mapper.OrderMapper;
import com.comfort.hotel.mapper.ParkingMapper;
import com.comfort.hotel.mapper.RoomMapper;
import com.comfort.hotel.mapper.UserMapper;
import com.comfort.hotel.mapper.WaiterMapper;
import com.comfort.hotel.service.OrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: OrderServiceImpl
 * @Description: 下单管理接口实现类
 * @Version: V1.0.0
 * @Author: ayuan
 * @Date: 2024/10/12 11:23
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private UserMapper userMapper;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private WaiterMapper waiterMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private CanteenMapper canteenMapper;
    @Autowired
    private ParkingMapper parkingMapper;

    // 分页查询当前用户订单信息
    @Override
    public PageInfo<OrderVO> findOrdersInList(PageVO pageVO) {

        // 第几页
        Integer page = pageVO.getCurrentPage();
        // 每页显示的大小
        Integer size = pageVO.getSize();
        // 使用pageHelper提高编码效率
        // 默认查询所有记录，PageHelper分页拦截器拦截，即在执行相关Sql之前会拦截自动加上limit
        PageHelper.startPage(page, size, Constants.BOOK_TIME_ORDER_BY_DESC);
        List<OrderVO> list = orderMapper.selectCurrentUserOrderList(pageVO.getKeyword(), pageVO.getUserId());

        return new PageInfo<>(list);
    }

    // 删除用户订单信息
    @Override
    public void deleteCurrentOrderInfo(String userId, String orderId) {
        orderMapper.hiddenUserCurrentInfo(userId, orderId);
    }

    // 支付当前订单
    @Override
    @Transactional
    public void payCurrentOrder(String userId, String orderId, Float orderSpend) {
        UserVO userVO = new UserVO();
        OrderVO orderVO = new OrderVO();
        WaiterVO waiterVO = new WaiterVO();
        OrderVO orderVO1 = orderMapper.selectOrderById(orderId);
        String waiterId = orderVO1.getWaiterId();
        // 更新用户代缴费信息
        Integer lastTotalCost = orderMapper.selectSpendAmountById(userId);
        // 泊车在下单前不会进行对应的操作
        if (orderSpend == null) {
            // 如果当前订单为泊车服务订单的话，管理员没有进行金额的设置, 那我们会根据时间的长度进行设置
            CarVO carVO = parkingMapper.selectCurrentCarParkingInfo(orderVO1.getObjectId());
            Integer spendAmount = carVO.getCumulativeCost();
            if (spendAmount == null || spendAmount == 0) {
                CarVO carVO1 = new CarVO();
                carVO1.setParkingId(orderVO1.getObjectId());
                long durationInSeconds = ChronoUnit.SECONDS.between(carVO.getLastParkingTime(),LocalDateTime.now());
                int hours = (int) durationInSeconds / 3600;
                int minutes = (int) (durationInSeconds % 3600) / 60;
                int cost = 0;
                if (hours > 24) {
                    cost = 1 +  hours - 24 + 3 * minutes;
                } else {
                    cost =  hours + 5 * minutes;
                }
                carVO1.setCumulativeCost(cost);
                userVO.setSpendAmount(cost);
                parkingMapper.updateCarParkingInfo(carVO1);
                orderVO.setOrderSpend((float) cost);
            } else {
                // 重新计算当前订单总金额
                userVO.setSpendAmount(lastTotalCost + spendAmount);
                orderVO.setOrderSpend(Float.valueOf(spendAmount));
            }
        } else {
            if (lastTotalCost == null) {
                lastTotalCost = 0;
            }
            // 重新计算当前订单总金额
            userVO.setSpendAmount((int) (lastTotalCost + orderSpend));
        }
        // 设置订单情况
        orderVO.setOrderInfo(Constants.PAY_SUCCESS);
        // 设置订单的支付时间
        orderVO.setPurchaseTime(new DateTime().toLocalDateTime());
        // 设置相关的id信息
        orderVO.setOrderId(orderId);
        orderVO.setUserId(userId);
        // 更新订单支付状态
        orderVO.setOrderStatus(Constants.ONE);
        orderMapper.updateCurrentOrder(orderVO);
        // 更新支付状态
        userVO.setPayStatus(Constants.ONE);
        userVO.setUserId(userId);
        userMapper.updateUserInfo(userVO);
        // 更新服务员状态 -- 非泊车的服务员
        if (!orderVO1.getObjectStatus().equals(Constants.TWO)) {
            waiterVO.setWaiterId(waiterId);
            waiterVO.setWaiterStatus(Constants.ONE);
            waiterMapper.updateWaiterInfo(waiterVO);
        }
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> executeAfterBooking(waiterId), Constants.THIRTY_MIN, TimeUnit.MINUTES);
    }

    // 模拟服务员都完成任务的时间, 并修改状态
    private void executeAfterBooking(String waiterId) {
        System.out.println("服务员完成任务状态修改开始执行，编号为：" + waiterId);
        WaiterVO waiterVO = new WaiterVO();
        waiterVO.setWaiterId(waiterId);
        waiterVO.setWaiterStatus(Constants.ZERO);
        waiterMapper.updateWaiterInfo(waiterVO);
        System.out.println("服务员完成任务状态修改结束");
    }

    // 导出用户订单信息
    @Override
    public List<OrderVO> exportOrderList(String userId) {
        return orderMapper.selectCurrentUserOrderList(null, userId);
    }

    // 取消当前订单
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelCurrentOrder(String userId, String orderId, String objectId, Integer objectStatus, Integer orderAmount, boolean isAutoCancel) {
        OrderVO orderVO = new OrderVO();
        UserVO userVO = new UserVO();
        WaiterVO waiterVO = new WaiterVO();
        OrderVO originalOrder = orderMapper.selectOrderById(orderId);
        try {
            switch (objectStatus) {
                // 修改订单的状态
                // 找到对应的商品，把商品的数量、预定状态进行还原
                case 0:
                    RoomVO roomVO = new RoomVO();
                    roomVO.setRoomId(objectId);
                    // 恢复默认的房间状态
                    roomVO.setRoomStatus(Constants.ZERO);
                    // 清除用户表中的房间id信息
                    userMapper.clearUserRoomId(userId);
                    roomMapper.updateRoomInfo(roomVO);
                    break;
                case 1:
                    CanteenVO canteenVO = new CanteenVO();
                    canteenVO.setCanteenId(objectId);
                    if (canteenMapper.selectCanteenTableStatus(objectId).equals(Constants.ONE)) {
                        canteenVO.setCanteenTableStatus(Constants.ZERO);
                    }
                    canteenVO.setCanteenNumberOfDiners(canteenMapper.selectCanteenNumberOfDinersById(objectId) - orderAmount);
                    canteenMapper.updateCanteenInfo(canteenVO);
                    break;
                case 3:
                    Integer presentAmount = goodsMapper.selectGoodsTotalAmountById(objectId);
                    GoodsVO goodsVO = new GoodsVO();
                    // 恢复原来的商品数目
                    goodsVO.setGoodsAmount(presentAmount + orderAmount);
                    goodsVO.setGoodsId(objectId);
                    // 如果商品数量从 无到有的话， 那还得恢复原来的状态
                    if (presentAmount.equals(Constants.ZERO)) {
                        goodsVO.setGoodsStatus(Constants.ZERO);
                    }
                    goodsMapper.updateGoodsInfo(goodsVO);
                    break;
            }
            // 修改订单的状态
            orderVO.setOrderStatus(Constants.TWO);
            // 记录取消时间
            orderVO.setExpiredTime(new DateTime().toLocalDateTime());
            // 记录订单的情况
            if (isAutoCancel) {
                orderVO.setOrderInfo(Constants.CANCEL_AUTOMATED);
            } else {
                orderVO.setOrderInfo(Constants.CANCEL_BY_HUMAN);
            }
            orderVO.setUserId(userId);
            orderVO.setOrderId(orderId);
            // 清除服务员记录
            orderMapper.updateWaiterByUserId(orderId);
            // 修改当前服务员状态
            waiterVO.setWaiterStatus(Constants.ZERO);
            waiterVO.setWaiterId(originalOrder.getWaiterId());
            waiterMapper.updateWaiterInfo(waiterVO);
            orderMapper.updateCurrentOrder(orderVO);
            return true;
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    // 查询当前停车订单信息
    @Override
    public ParkingInfoVO searchCurrentParkingInfo(String userId) {
        // 查找当前用户的停车信息，状态为停放中的汽车
        OrderVO order = orderMapper.selectOrderByUserId(userId);
        if (order != null) {
            // 获取当前用户当次车辆停放信息
            CarVO carVO = parkingMapper.selectCurrentCarParkingInfo(order.getObjectId());   // 查询服务员名称
            String waiterName = waiterMapper.selectWaiterNameById(order.getWaiterId());
            if (carVO != null) {
                ParkingInfoVO parkingInfoVO = new ParkingInfoVO();
                parkingInfoVO.setCarCover(carVO.getCarCover());
                parkingInfoVO.setCarName(carVO.getCarName());
                parkingInfoVO.setCarNumber(carVO.getCarNumber());
                parkingInfoVO.setCarStatus(carVO.getCarStatus());
                parkingInfoVO.setCumulativeCost(carVO.getCumulativeCost());
                parkingInfoVO.setLastParkingTime(carVO.getLastParkingTime());
                parkingInfoVO.setLastLeaveTime(carVO.getLastLeaveTime());
                parkingInfoVO.setParkingBoy(waiterName);
                return parkingInfoVO;
            }
        }
        return null;
    }
}