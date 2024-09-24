package com.comfort.hotel.service;

import com.comfort.hotel.domain.dto.CarDTO;
import com.comfort.hotel.domain.dto.PageDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: ayuan
 * @PS: 车辆停放信息接口
 * @Date: 2024/9/16 11:07
 */
public interface ParkingService {

    // 删除当前车辆停放信息
    void deleteCurrentCarParkingInfo(String carId);

    // 更新车辆停放信息基本信息
    void updateCarParkingInfo(CarDTO carDTO);

   // 更新车辆停发图片
    void uploadCarParkingCover(MultipartFile file);

   // 分页查询车辆停放信息
    PageInfo<CarDTO> findCarParkingInList(PageDTO pageDTO);
}
