package com.comfort.hotel.mapper;

import com.comfort.hotel.domain.dto.GoodsDTO;
import com.comfort.hotel.domain.dto.RoomDTO;
import com.comfort.hotel.domain.vo.RoomVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @Author: ayuan
 * @PS: 客房管理mapper
 * @Date: 2024/9/13 17:56
 */
public interface RoomMapper {

    // 更新客房基本信息
    int updateRoomInfo(@Param("bean") RoomVO roomVO);

    // 查询房间列表集合
    List<RoomVO> selectRoomList(@Param("keyword") String keyword);

    // 根据id删除当前客房
    @Delete("delete from room where room_id = #{roomId}")
    void deleteRoomById(@Param("roomId") String roomId);


    // 修改当前房间状态
    @Update("update room set room_status = #{roomStatus} where room_id = #{roomId}")
    void changeRoomStatus(@Param("roomStatus") Integer roomStatus, @Param("roomId") String roomId);

    // 录入房间信息
    void addRoomInfo(@Param("bean") RoomVO roomVO);

    // 判断客房图片文件路径是否已经存在
    @Select("select count(1) from room where room_cover = #{roomCover}")
    Integer isImgUrlPathExist(@Param("roomCover") String roomCover);

    // 判断待添加的房间号是否已经存在
    @Select("select count(1) from room where room_mark = #{roomMark}")
    Integer checkRoomMarkIsExist(@Param("roomMark") String roomMark);

    // 通过id获取客房号
    @Select("select room_mark from room where room_id = #{roomId}")
    String getRoomMarkById(@Param("roomId") String roomId);

    // 根据条件获取当前房间的数量信息
    Integer distinguishVariableRoom(@Param("types") Integer[] types, @Param("status") Integer status);

    // 查询当前房间信息
    RoomDTO selectRoomById(@Param("roomId") String roomId);

    // 查询当前房间点赞数
    @Select("select room_trump_up from room where room_id = #{roomId}")
    Integer selectTrumpUp(@Param("roomId") String roomId);

    // 查询当前房间踩数
    @Select("select room_trump_down from room where room_id = #{roomId}")
    Integer selectTrumpDown(@Param("roomId") String roomId);
}
