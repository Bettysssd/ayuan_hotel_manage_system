package com.comfort.hotel.mapper;

import com.comfort.hotel.domain.dto.PageDTO;
import com.comfort.hotel.domain.dto.RoomDTO;
import com.comfort.hotel.domain.dto.WaiterDTO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @Author: ayuan
 * @PS: 服务员管理mapper
 * @Date: 2024/9/13 17:56
 */
public interface WaiterMapper {

    // 更新服务员基本信息
    void updateWaiterInfo(@Param("bean") WaiterDTO waiterDTO);

    // 查询服务员列表集合
    List<WaiterDTO> selectWaiterList(@Param("keyword") String keyword, @Param("jobTypes") int[] jobTypes);

    // 根据id删除当前服务员
    @Delete("delete from waiter where waiter_id = #{waiterId}")
    void deleteWaiterById(@Param("waiterId") String waiterId);

    // 录入服务员信息
    void addWaiterInfo(@Param("bean") WaiterDTO waiterDTO);

    // 判断服务员图片文件路径是否已经存在
    @Select("select count(1) from waiter where waiter_cover = #{waiterCover}")
    Integer isImgUrlPathExist(@Param("waiterCover") String waiterCover);

    // 根据工作类型查询不同的管理员人数
    Integer getWaiterCountByType(@Param("types") Integer[] types);
}
