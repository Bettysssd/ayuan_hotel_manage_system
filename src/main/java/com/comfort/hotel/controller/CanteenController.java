package com.comfort.hotel.controller;
import com.comfort.hotel.domain.vo.CanteenVO;
import com.comfort.hotel.domain.vo.PageVO;
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
     * @param pageVO
     * @return
     */
    @ApiOperation(value = "分页查询食堂信息")
    @PostMapping("/queryCanteenList")
    public ResponseVO<PageInfo<CanteenVO>> queryCanteenList(@RequestBody(required = false) PageVO pageVO) {
        PageInfo<CanteenVO> canteenInList = canteenService.findCanteenInList(pageVO);
        return ResponseVO.success(canteenInList);
    }




    /**
     * 更新食堂信息
     *
     * @param canteenVO
     */
    @ApiOperation(value = "更新食堂信息")
    @PostMapping("/updateCanteenInfo")
    public void updateCarParkingInfo(@RequestBody CanteenVO canteenVO) {
        canteenService.updateCanteenInfo(canteenVO);
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
     * @param canteenVO
     */
    @ApiOperation(value = "录入食堂信息")
    @PostMapping("/addCanteenInfo")
    public void addCanteenInfo(@RequestBody CanteenVO canteenVO) {
        canteenService.addCanteenInfo(canteenVO);
    }

}
