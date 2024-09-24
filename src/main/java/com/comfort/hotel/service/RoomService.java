package com.comfort.hotel.service;

import com.comfort.hotel.domain.dto.PageDTO;
import com.comfort.hotel.domain.dto.RoomDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: ayuan
 * @PS: 客房管理接口
 * @Date: 2024/9/13 17:48
 */

public interface RoomService {

    // 更新客房图片
    void uploadRoomCover(MultipartFile file);

    // 更新客房基本信息
    boolean updateRoomInfo(RoomDTO roomDTO);

    // 删除当前客房
    void deleteCurrentRoom(String roomId);

    // 修改当前房间状态
    void changeRoomStatus(RoomDTO roomDTO);

    // 录入房间信息
    boolean addRoomInfo(RoomDTO roomDTO);

    // 分页查询房间信息
    PageInfo<RoomDTO> findRoomInList(PageDTO pageDTO);

    // 根据条件获取当前房间的数量信息
    Integer distinguishVariableRoom(Integer[] types, Integer status);
}
