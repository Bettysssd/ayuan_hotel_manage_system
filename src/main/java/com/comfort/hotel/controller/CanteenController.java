package com.comfort.hotel.controller;
import com.comfort.hotel.domain.dto.CanteenDTO;
import com.comfort.hotel.domain.dto.PageDTO;
import com.comfort.hotel.domain.vo.ResponseVO;
import com.comfort.hotel.service.CanteenService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;


/**
 * @Author: ayuan
 * @PS: 食堂控制管理类
 * @Date: 2024/9/18 11:37
 */
@RequestMapping("/supervisor")
@RestController
@Api(tags = "食堂管理相关业务")
public class CanteenController {

    @Resource
    private CanteenService canteenService;


    /**
     * 分页查询食堂信息
     *
     * @param pageDTO
     * @return
     */
    @ApiOperation(value = "分页查询食堂信息")
    @PostMapping("/queryCanteenList")
    public ResponseVO<PageInfo<CanteenDTO>> queryCanteenList(@RequestBody PageDTO pageDTO) {
        PageInfo<CanteenDTO> canteenInList = canteenService.findCanteenInList(pageDTO);
        return ResponseVO.success(canteenInList);
    }




    /**
     * 更新食堂信息
     *
     * @param canteenDTO
     */
    @ApiOperation(value = "更新食堂信息")
    @PostMapping("/updateCanteenInfo")
    public void updateCarParkingInfo(@RequestBody CanteenDTO canteenDTO) {
        canteenService.updateCanteenInfo(canteenDTO);
    }

    /**
     * 更新食堂图片
     */
    @ApiOperation(value = "更新食堂图片")
    @PostMapping("/uploadCanteenCover")
    public void uploadCanteenCover(@RequestParam("file") MultipartFile file) {
        canteenService.uploadCanteenCover(file);
    }

    /**
     * 删除当前食堂信息
     *
     * @param canteenId
     */
    @ApiOperation(value = "删除当前食堂信息")
    @DeleteMapping("/deleteCurrentCanteenInfo")
    public void deleteCurrentCanteenInfo(@RequestParam("canteenId") String canteenId) {
        canteenService.deleteCurrentCanteenInfo(canteenId);
    }


    /**
     * 录入食堂信息
     * @param canteenDTO
     */
    @ApiOperation(value = "录入食堂信息")
    @PostMapping("/addCanteenInfo")
    public void addCanteenInfo(@RequestBody CanteenDTO canteenDTO) {
        canteenService.addCanteenInfo(canteenDTO);
    }

}
