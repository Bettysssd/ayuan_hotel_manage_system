package com.comfort.hotel.controller;

import com.comfort.hotel.domain.vo.PageVO;
import com.comfort.hotel.domain.vo.ReviewVO;
import com.comfort.hotel.domain.vo.WaiterVO;
import com.comfort.hotel.domain.vo.ResponseVO;
import com.comfort.hotel.service.WaiterService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @Author: ayuan
 * @PS: 服务员管理控制类
 * @Date: 2024/9/13 17:46
 */
@RequestMapping("/supervisor")
@RestController
@Api(tags = "服务员管理相关业务")
public class WaiterController {

    @Resource
    private WaiterService waiterService;

    @ApiOperation(value = "查找不同状态下泊车员的数量")
    @GetMapping("/distinguishParkingByInStatus")
    public ResponseVO<Integer> getParkingByStatus(@RequestParam("status") Integer status) {
        return ResponseVO.success(waiterService.getParkingByStatus(status));
    }


    /**
     * 根据工作类型查询不同的管理员人数
     * @param types
     * @return
     */
    @ApiOperation(value = "根据工作类型查询不同的管理员人数")
    @GetMapping("/distinguishWaiterByType")
    public ResponseVO<Integer> getWaiterCountByType(@RequestParam("types") Integer[] types){
        return ResponseVO.success(waiterService.getWaiterCountByType(types));
    }



    /**
     * 录入服务员信息
     * @param waiterVO
     */
    @ApiOperation(value = "录入服务员信息")
    @PostMapping("/addWaiterInfo")
    public void addWaiterInfo(@RequestBody WaiterVO waiterVO) {
        waiterService.addWaiterInfo(waiterVO);
    }


    /**
     * 删除当前服务员
     *
     * @param waiterId
     */
    @ApiOperation(value = "删除当前服务员")
    @DeleteMapping("/deleteCurrentWaiter")
    public void deleteCurrentWaiter(@RequestParam("waiterId") String waiterId) {
        waiterService.deleteCurrentWaiter(waiterId);
    }

    /**
     * 查看当前服务员和商品信息
     *
     * @param waiterId
     */
    @ApiOperation(value = "查看当前服务员和商品信息")
    @GetMapping("/showCurrentWaiterAndGoods")
    public ResponseVO<ReviewVO> showCurrentWaiterAndGoods(@RequestParam("waiterId") String waiterId,
                                                          @RequestParam("objectId") String objectId,
                                                          @RequestParam("objectStatus") Integer objectStatus) {
        return ResponseVO.success(waiterService.showCurrentWaiterAndGoods(waiterId, objectId, objectStatus));
    }

    /**
     * 评价当前服务员和商品
     * @param waiterId
     * @param objectId
     * @param status
     * @return
     */
    @ApiOperation(value = "评价当前服务员和商品")
    @GetMapping("/review")
    public ResponseVO review(@RequestParam("waiterId") String waiterId, @RequestParam("objectId") String objectId,
                             @RequestParam("orderId") String orderId, @RequestParam("userId") String userId,
                             @RequestParam("objectStatus") Integer objectStatus, @RequestParam("status") Integer status) {
       boolean result = waiterService.reviewCurrentWaiterAndGoods(waiterId, objectId, orderId, userId, objectStatus, status);
       if (result) {
           return ResponseVO.success();
       } else {
           return ResponseVO.error("评论失败");
       }


    }



    /**
     * 更新服务员基本信息
     *
     * @param waiterVO
     */
    @ApiOperation(value = "更新服务员基本信息")
    @PostMapping("/updateWaiterInfo")
    public void updateWaiterInfo(@RequestBody WaiterVO waiterVO) {
       waiterService.updateWaiterInfo(waiterVO);
    }

    /**
     * 更新服务员图片
     */
    @ApiOperation(value = "更新服务员图片")
    @PostMapping("/uploadWaiterCover")
    public void uploadWaiterCover(@RequestParam("file") MultipartFile file) {
        waiterService.uploadWaiterCover(file);
    }

    /**
     * 分页查询服务员信息
     *
     * @param pageVO
     * @return
     */
    @ApiOperation(value = "分页服务员信息")
    @PostMapping("/queryWaiterList")
    public ResponseVO<PageInfo<WaiterVO>> queryWaiterList(@RequestBody PageVO pageVO) {
        PageInfo<WaiterVO> waiterInList = waiterService.findWaiterInList(pageVO);
        return ResponseVO.success(waiterInList);
    }

}
