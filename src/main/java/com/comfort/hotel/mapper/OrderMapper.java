package com.comfort.hotel.mapper;

import com.comfort.hotel.domain.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @ClassName: OrderMapper
 * @Description: 订单mapper
 * @Version: V1.0.0
 * @Author: ayuan
 * @Date: 2024/10/12 11:25
 *
 */
@Mapper
public interface OrderMapper {

    // 假删用户订单信息
    @Update("update `order` set display_status = 0 where user_id = #{userId} and order_id = #{orderId} ")
    void hiddenUserCurrentInfo(@Param("userId") String userId, @Param("orderId") String orderId);

    // 支付当前订单
    int updateCurrentOrder(@Param("bean") OrderVO orderVO);

    //查询当前用户的先前的代缴金额
    @Select("select spend_amount from user where user_id = #{userId}")
    Integer selectSpendAmountById(@Param("userId") String userId);

    //查询当前用户的所有订单
    List<OrderVO> selectCurrentUserOrderList(@Param("keyword") String keyword, @Param("userId") String userId);

    // 添加一条订单信息
    void addNewOrderInfo(@Param("bean") OrderVO orderVO);

    // 查询当前订单的状态
    @Select("select order_status from `order` where order_id = #{orderId}")
    Integer selectCurrentOrderStatus(@Param("orderId") String orderId);

    @Select("select order_status from `order` where object_id = #{roomId}")
    Integer selectOrderStatus(@Param("roomId") String roomIdInRental);

    // 根据订单Id查询对应的订单
    OrderVO selectOrderById(@Param("orderId") String orderId);

    // 根据多重Id查询对应的订单
    @Select(" SELECT order_id FROM `order` WHERE user_id = #{userId} and object_status = #{objectStatus} and order_status = 0")
    String selectOrderByMoreCondition(@Param("userId") String userId, @Param("objectStatus") Integer objectStatus);

    // 根据id清除服务员
    @Update("update `order` set waiter_id = null where user_id = #{orderId}")
    void updateWaiterByUserId(@Param("orderId") String orderId);


    OrderVO selectOrderByUserId(@Param("userId") String userId);
}