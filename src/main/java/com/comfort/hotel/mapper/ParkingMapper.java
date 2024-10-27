package com.comfort.hotel.mapper;

import com.comfort.hotel.domain.vo.CarVO;
import com.comfort.hotel.domain.vo.OrderVO;
import com.comfort.hotel.domain.vo.ParkingInfoVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: ayuan
 * @PS: 停车信息管理mapper
 * @Date: 2024/9/13 17:56
 */
@Mapper
public interface ParkingMapper {

    // 更新停车基本信息
    void updateCarParkingInfo(@Param("bean") CarVO carVO);

    // 查询停车列表集合
    List<CarVO> selectParkingCarList(@Param("keyword") String keyword);

    // 根据id当前车辆停放信息
    @Delete("delete from parking where parking_id = #{parkingId}")
    void deleteCurrentCarParkingInfo(@Param("parkingId") String parkingId);


    // 判断车辆图片文件路径是否已经存在
    @Select("select count(1) from parking where car_cover = #{carCover}")
    Integer isImgUrlPathExist(@Param("carCover") String carCover);


    // 获取当前用户当次车辆停放信息
    CarVO selectCurrentCarParkingInfo(@Param("parkingId") String objectId);

    // 添加车辆信息
    void insertCarInfo(@Param("bean") CarVO carVO);
}
