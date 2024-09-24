package com.comfort.hotel.controller;

import com.comfort.hotel.domain.dto.CarDTO;
import com.comfort.hotel.domain.dto.PageDTO;
import com.comfort.hotel.domain.dto.WaiterDTO;
import com.comfort.hotel.domain.vo.ResponseVO;
import com.comfort.hotel.service.ParkingService;
import com.comfort.hotel.service.WaiterService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @Author: ayuan
 * @PS: 停车信息管理控制类
 * @Date: 2024/9/13 17:46
 */
@RequestMapping("/supervisor")
@RestController
@Api(tags = "停车信息管理相关业务")
public class ParkingController {

    @Resource
    private ParkingService parkingService;


    /**
     * 删除当前车辆停放信息
     *
     * @param parkingId
     */
    @ApiOperation(value = "删除当前车辆停放信息")
    @DeleteMapping("/deleteCurrentCarParkingInfo")
    public void deleteCurrentCarParkingInfo(@RequestParam("parkingId") String parkingId) {
        parkingService.deleteCurrentCarParkingInfo(parkingId);
    }


    /**
     * 更新车辆停放信息
     *
     * @param carDTO
     */
    @ApiOperation(value = "更新车辆停放信息")
    @PostMapping("/updateCarParkingInfo")
    public void updateCarParkingInfo(@RequestBody CarDTO carDTO) {
        parkingService.updateCarParkingInfo(carDTO);
    }

    /**
     * 更新车辆停发图片
     */
    @ApiOperation(value = "更新车辆停发图片")
    @PostMapping("/uploadCarParkingCover")
    public void uploadCarParkingCover(@RequestParam("file") MultipartFile file) {
        parkingService.uploadCarParkingCover(file);
    }

    /**
     * 分页查询车辆停放信息
     *
     * @param pageDTO
     * @return
     */
    @ApiOperation(value = "分页查询车辆停放信息")
    @PostMapping("/queryCarParkingList")
    public ResponseVO<PageInfo<CarDTO>> queryWaiterList(@RequestBody PageDTO pageDTO) {
        PageInfo<CarDTO> carParkingInList = parkingService.findCarParkingInList(pageDTO);
        return ResponseVO.success(carParkingInList);
    }

}
