package com.comfort.hotel.mapper;

import com.comfort.hotel.domain.dto.UserDTO;
import com.comfort.hotel.domain.dto.UserPayDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Author: ayuan
 * @PS: 用户管理mapper
 * @Date: 2024/8/29 17:49
 */
@Mapper
public interface UserMapper {

    // 用户注册
    @Insert("insert into user (user_id, email, password) " +
            "values (#{userId}, #{qqAccount}, #{password})")
    void signInUser(@Param("userId") String userId,
                    @Param("qqAccount") String qqAccount,
                    @Param("password") String password);

    // 通过用户邮箱查找用户id
    @Select("select user_id from user where email = #{email}")
    String selectUserIdByEmail(@Param("email") String email);

    // 通过用户的邮箱查找用户的密码
    @Select("select password from user where email = #{email}")
    String selectPasswordByAccount(@Param("email") String email);

    // 通过用户的id更新用户最近的登录时间
    @Update("update user set last_login_time = #{lastLoginTime} where user_id = #{userId}")
    void updateUserLoginTimeByUserId(@Param("lastLoginTime") String lastLoginTime, @Param("userId") String userId);

    // 为用户更新密码
    @Update("update user set password = #{newPwd} where user_id = #{userId}")
    Integer updateUserPasswordById(@Param("userId") String userId, @Param("newPwd") String newPwd);

    // 查询用户列表集合
    List<UserDTO> selectUserList(@Param("keyword") String keyword);

    // 获取用户姓名
    @Select("select user_name from user where user_id = #{userId}")
    String getUserName(@Param("userId") String userId);

    // 修改用户当前状态
    @Update("update user set user_status = #{userStatus} where user_id = #{userId}")
    void changeUserStatusById(@Param("userStatus") Integer userStatus,@Param("userId") String userId);

    //恢复默认用户头像
    @Update("update user set user_cover = #{imgUrl} where user_id = #{userId}")
    void recoverDefaultImage(@Param("imgUrl") String imgUrl, @Param("userId") String userId);

    // 根据车辆id查询用户原有的消费
    @Select("select spend_amount from user where parking_id = #{parkingId}")
    Integer getUserCost(@Param("parkingId") String parkingId);

    // 通过车辆id更新用户表当前的消费费用
    @Update("update user set spend_amount = #{recentCosted} where parking_id = #{parkingId}")
    void updateUserPresentCost(@Param("recentCosted")Integer recentCosted, @Param("parkingId") String parkingId);

    // 通过用户特征分类用户
    Integer distinguishUserByCondition(@Param("type") Integer type, @Param("status") Integer status);

    // 异步获取不同状态下的用户消费情况
    List<UserPayDTO> getUserDifferentPaySituation(@Param("status") Integer status);
}
