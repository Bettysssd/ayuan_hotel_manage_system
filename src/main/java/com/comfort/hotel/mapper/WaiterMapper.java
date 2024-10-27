package com.comfort.hotel.mapper;

import com.comfort.hotel.domain.dto.WaiterDTO;
import com.comfort.hotel.domain.vo.ReviewVO;
import com.comfort.hotel.domain.vo.WaiterVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: ayuan
 * @PS: 服务员管理mapper
 * @Date: 2024/9/13 17:56
 */
public interface WaiterMapper {

    // 更新服务员基本信息
    int updateWaiterInfo(@Param("bean") WaiterVO waiterVO);

    // 查询服务员列表集合
    List<WaiterVO> selectWaiterList(@Param("keyword") String keyword, @Param("jobTypes") int[] jobTypes);

    // 根据id删除当前服务员
    @Delete("delete from waiter where waiter_id = #{waiterId}")
    void deleteWaiterById(@Param("waiterId") String waiterId);

    // 录入服务员信息
    void addWaiterInfo(@Param("bean") WaiterVO waiterVO);

    // 判断服务员图片文件路径是否已经存在
    @Select("select count(1) from waiter where waiter_cover = #{waiterCover}")
    Integer isImgUrlPathExist(@Param("waiterCover") String waiterCover);

    // 根据工作类型查询不同的管理员人数
    Integer getWaiterCountByType(@Param("types") Integer[] types);


    @Select("select waiter_id from waiter where waiter_status = #{waiterStatus} and job_type = #{jobType}")
    List<String> selectWantedWaiter(@Param("waiterStatus") Integer waiterStatus, @Param("jobType") Integer jobType);

    // 根据状态查询不同状态的泊车员人数
    @Select("select count(1) from waiter where waiter_status = #{status} and job_type = 2")
    Integer getParkingByStatus(@Param("status") Integer status);

    // 根据id查询服务员名称
    @Select("select waiter_name from waiter where waiter_id = #{waiterId}")
    String selectWaiterNameById(@Param("waiterId") String waiterId);

    // 查看当前服务员信息
    WaiterDTO selectWaiterById(@Param("waiterId") String waiterId);

    // 点赞数
    @Select("select waiter_trump_up from waiter where waiter_id = #{waiterId}")
    Integer selectTrumpUp(@Param("waiterId") String waiterId);

    // 点赞数
    @Select("select waiter_trump_down from waiter where waiter_id = #{waiterId}")
    Integer selectTrumpDown(@Param("waiterId") String waiterId);
}
