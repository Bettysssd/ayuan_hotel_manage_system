package com.comfort.hotel.controller;

import com.comfort.hotel.constants.Constants;
import com.comfort.hotel.domain.vo.PageVO;
import com.comfort.hotel.domain.vo.RoomVO;
import com.comfort.hotel.domain.vo.ResponseVO;
import com.comfort.hotel.service.RoomService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: ayuan
 * @PS: 客房管理控制类
 * @Date: 2024/9/13 17:46
 */
@RequestMapping("/supervisor")
@RestController
@Api(tags = "客房管理相关业务")
public class RoomController {

    @Resource
    private RoomService roomService;

    /**
     * 根据条件获取当前房间的数量信息
     * @param types
     * @param status
     */
    @ApiOperation(value = "根据条件获取当前房间的数量信息")
    @GetMapping("/distinguishVariableRoom")
    public ResponseVO<Integer> distinguishVariableRoom(@RequestParam("types") Integer[] types, @RequestParam("status") Integer status) {
        return ResponseVO.success(roomService.distinguishVariableRoom(types,status));
    }

    /**
     * 录入房间信息
     *
     * @param roomVO
     */
    @ApiOperation(value = "录入房间信息")
    @PostMapping("/addRoomInfo")
    public void addRoomInfo(@RequestBody RoomVO roomVO, HttpServletResponse response) {
        if (roomService.addRoomInfo(roomVO)) {
            // 设置失败信息标识
            response.setHeader(Constants.ROOM_ADD_ERROR,"unable");
        }
    }


    /**
     * 修改当前房间状态
     *
     * @param roomVO
     */
    @ApiOperation(value = "修改当前房间状态")
    @PostMapping("/changeRoomStatus")
    public void changeRoomStatus(@RequestBody RoomVO roomVO) {
        roomService.changeRoomStatus(roomVO);
    }

    /**
     * 删除当前客房
     *
     * @param roomId
     */
    @ApiOperation(value = "删除当前客房")
    @DeleteMapping("/deleteCurrentRoom")
    public void deleteCurrentRoom(@RequestParam("roomId") String roomId, HttpServletResponse response) {
        roomService.deleteCurrentRoom(roomId);
    }


    /**
     * 更新客房基本信息
     *
     * @param roomVO
     */
    @ApiOperation(value = "更新客房基本信息")
    @PostMapping("/updateRoomInfo")
    public void updateRoomInfo(@RequestBody RoomVO roomVO, HttpServletResponse response ) {
        if (!roomService.updateRoomInfo(roomVO)) {
            // 设置失败信息标识
            response.setHeader(Constants.ROOM_UPDATE_ERROR,"unable");
        }
    }

    /**
     * 更新客房图片
     */
    @ApiOperation(value = "更新客房图片")
    @PostMapping("/uploadRoomCover")
    public void uploadRoomCover(@RequestParam("file") MultipartFile file) {
        roomService.uploadRoomCover(file);
    }

    /**
     * 分页查询房间信息
     *
     * @param pageVO
     * @return
     */
    @ApiOperation(value = "分页查询房间信息")
    @PostMapping("/queryRoomList")
    public ResponseVO<PageInfo<RoomVO>> queryRoomList(@RequestBody PageVO pageVO) {
        PageInfo<RoomVO> roomInList = roomService.findRoomInList(pageVO);
        return ResponseVO.success(roomInList);
    }

}
