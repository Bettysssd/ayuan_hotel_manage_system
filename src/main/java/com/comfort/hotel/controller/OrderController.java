package com.comfort.hotel.controller;
import cn.hutool.core.date.DateUtil;
import com.comfort.hotel.domain.vo.OrderVO;
import com.comfort.hotel.domain.vo.PageVO;
import com.comfort.hotel.domain.vo.ParkingInfoVO;
import com.comfort.hotel.domain.vo.ResponseVO;
import com.comfort.hotel.service.OrderService;
import com.comfort.hotel.utils.ExportExcelUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Author: ayuan
 * @PS: 商品预定订单控制管理类
 * @Date: 2024/9/18 11:37
 */

@Api(tags = "用户相关业务")
@RestController
@RequestMapping("/user")
public class OrderController {


    @Resource
    private OrderService orderService;


    /**
     * 查询当前停车订单信息
     * @param userId
     * @return
     */
    @ApiOperation(value = "查询当前停车订单信息")
    @GetMapping("/searchCurrentParkingInfo")
    public ResponseVO<ParkingInfoVO> searchCurrentParkingInfo(@RequestParam("userId") String userId) {
         ParkingInfoVO parkingInfo =  orderService.searchCurrentParkingInfo(userId);
        if (!(parkingInfo == null)) {
            return ResponseVO.success(parkingInfo);
        } else {
            return ResponseVO.success();
        }
    }


    /**
     * 取消当前订单
     * @param userId
     * @param orderId
     * @param objectId
     * @param objectStatus
     * @param orderAmount
     * @return
     */
    @ApiOperation(value = "取消当前订单")
    @GetMapping("/cancelCurrentOrder")
    public ResponseVO cancelCurrentOrder(@RequestParam("userId") String userId, @RequestParam("orderId") String orderId,
                                         @RequestParam("objectId") String objectId, @RequestParam("objectStatus") Integer objectStatus,
                                         @RequestParam("orderAmount") Integer orderAmount) {
        boolean isCanceled = orderService.cancelCurrentOrder(userId, orderId, objectId, objectStatus, orderAmount, false);
        if (isCanceled) {
           return ResponseVO.success("取消当前订单成功");
        } else {
            return ResponseVO.error("取消当前订单失败");
        }
    }




    @ApiOperation(value = "导出用户全部订单信息")
    @GetMapping("/exportOrderList")
    public void exportOrderList(@RequestParam("userId") String userId, HttpServletResponse response) throws Exception {
        ArrayList<OrderVO> list = (ArrayList<OrderVO>) orderService.exportOrderList(userId);
        List<String[]> headerAlias = Arrays.asList(
                new String[]{"orderId", "订单ID"},
                new String[]{"objectId", "消费对象ID"},
                new String[]{"objectName", "消费对象名称"},
                new String[]{"orderSpend", "订单消费金额"},
                new String[]{"objectStatus", "消费对象属性"},
                new String[]{"orderAmount", "购买/预定数量"},
                new String[]{"orderStatus", "订单状态"},
                new String[]{"bookTime", "下单时间"},
                new String[]{"purchaseTime", "支付时间"},
                new String[]{"expiredTime", "取消/失效时间"},
                new String[]{"orderInfo", "订单情况"}
        );
        String title = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        ExportExcelUtil.exportExcel(response, title, headerAlias, list);
    }

    /**
     * 支付当前用户订单
     * @param userId
     * @param orderId
     */
    @ApiOperation(value = "支付当前用户订单")
    @GetMapping("/goToPay")
    public void goToPay(@RequestParam("userId") String userId, @RequestParam("orderSpend") Float orderSpend,
                        @RequestParam("orderId") String orderId) {
        orderService.payCurrentOrder(userId, orderId, orderSpend);
    }


    /**
     * 删除用户订单信息
     * @param userId
     * @param orderId
     * @return
     */
    @ApiOperation(value = "删除用户订单信息")
    @DeleteMapping("/deleteCurrentOrder")
    public void deleteCurrentOrderInfo(@RequestParam("userId") String userId,
                                       @RequestParam("orderId") String orderId) {
        orderService.deleteCurrentOrderInfo(userId, orderId);
    }

    /**
     * 分页查询当前用户订单信息
     * @param pageVO
     * @return
     */
    @ApiOperation(value = "分页查询当前用户订单信息")
    @PostMapping("/queryUserOrderList")
    public ResponseVO<PageInfo<OrderVO>> queryUserOrderList(@RequestBody PageVO pageVO) {
        PageInfo<OrderVO> ordersInlist = orderService.findOrdersInList(pageVO);
        return ResponseVO.success(ordersInlist);
    }

}