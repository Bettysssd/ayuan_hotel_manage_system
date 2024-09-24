package com.comfort.hotel.controller;

import com.comfort.hotel.constants.Constants;
import com.comfort.hotel.domain.dto.PageDTO;
import com.comfort.hotel.domain.dto.RoomDTO;
import com.comfort.hotel.domain.dto.WaiterDTO;
import com.comfort.hotel.domain.vo.ResponseVO;
import com.comfort.hotel.service.RoomService;
import com.comfort.hotel.service.WaiterService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

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
     * @param waiterDTO
     */
    @ApiOperation(value = "录入服务员信息")
    @PostMapping("/addWaiterInfo")
    public void addWaiterInfo(@RequestBody WaiterDTO waiterDTO) {
        waiterService.addWaiterInfo(waiterDTO);
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
     * 更新服务员基本信息
     *
     * @param waiterDTO
     */
    @ApiOperation(value = "更新服务员基本信息")
    @PostMapping("/updateWaiterInfo")
    public void updateWaiterInfo(@RequestBody WaiterDTO waiterDTO) {
       waiterService.updateWaiterInfo(waiterDTO);
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
     * @param pageDTO
     * @return
     */
    @ApiOperation(value = "分页服务员信息")
    @PostMapping("/queryWaiterList")
    public ResponseVO<PageInfo<WaiterDTO>> queryWaiterList(@RequestBody PageDTO pageDTO) {
        PageInfo<WaiterDTO> waiterInList = waiterService.findWaiterInList(pageDTO);
        return ResponseVO.success(waiterInList);
    }

}
