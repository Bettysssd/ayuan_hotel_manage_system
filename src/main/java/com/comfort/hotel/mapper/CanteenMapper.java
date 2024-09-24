package com.comfort.hotel.mapper;

import com.comfort.hotel.domain.dto.CanteenDTO;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalTime;
import java.util.List;

/**
 * @Author: ayuan
 * @PS: 食堂服务接口服务Mapper
 * @Date: 2024/9/18 11:50
 */
public interface CanteenMapper {

    // 分页查询食堂信息
    List<CanteenDTO> selectCanteenList(String keyword);

    // 更新食堂信息
    void updateCanteenInfo(@Param("bean") CanteenDTO canteenDTO);

    // 录入食堂信息
    void addCanteenInfo(@Param("bean") CanteenDTO canteenDTO);

    // 判断数据库中是否存在
    @Select("select count(1) from canteen where canteen_cover = #{canteenCover}")
    int isImgUrlPathExist(@Param("canteenCover") String canteenCover );

    // 根据id删除当前食堂信息
    @Delete("delete from canteen where canteen_id = #{canteenId}")
    void deleteCurrentCanteenInfoById(@Param("canteenId") String canteenId);


    // 查询所有的食堂id
    @Select("select canteen_id from canteen")
    List<String> selectAllCanteenIds();

    // 根据id查询食堂当前的状态
    @Select("select canteen_open_status from canteen where canteen_id = #{canteenId}")
    Integer selectCanteenStatusById(@Param("canteenId") String id);

    // 查询当前食堂的营业时间
    @Select("select canteen_open_time from canteen where canteen_id = #{canteenId} ")
    LocalTime selectOpenTimeById(@Param("canteenId") String id);

    // 查询当前食堂的打烊时间
    @Select("select canteen_closed_time from canteen where canteen_id = #{canteenId} ")
    LocalTime selectClosedTimeById(@Param("canteenId") String id);

    // 更新食堂当前状态
    @Update("update canteen set canteen_open_status = #{canteenOpenStatus} where canteen_id = #{canteenId}")
    Integer updateCanteenOpenStatus(@Param("canteenId") String id,@Param("canteenOpenStatus") Integer target);
}
