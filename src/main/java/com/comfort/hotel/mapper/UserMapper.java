package com.comfort.hotel.mapper;

import com.comfort.hotel.domain.entity.User;
import org.apache.ibatis.annotations.*;

/**
 * @Author: ayuan
 * @PS:
 * @Date: 2024/8/29 17:49
 */
@Mapper
public interface UserMapper {

    // 用户注册
    @Insert("insert into user (user_id, qq_email, password) " +
            "values (#{userId}, #{qqAccount}, #{password})")
    void signInUser(@Param("userId") String userId,
                    @Param("qqAccount") String qqAccount,
                    @Param("password") String password);

    // 通过用户邮箱查找用户id
    @Select("select user_id from user where qq_email = #{qqEmail}")
    String selectUserIdByQQEmail(@Param("qqEmail") String qqEmail);

    // 通过用户的邮箱查找用户的密码
    @Select("select password from user where qq_email = #{qqEmail}")
    String selectPasswordByQQAccount(@Param("qqEmail") String qqEmail);

    // 通过用户的id更新用户最近的登录时间
    @Update("update user set last_login_time = #{lastLoginTime} where user_id = #{userId}")
    void updateUserLoginTimeByUserId(@Param("lastLoginTime") String lastLoginTime, @Param("userId") String userId);

    // 为用户更新密码
    @Update("update user set password = #{newPwd} where user_id = #{userId}")
    Integer updateUserPasswordById(@Param("userId") String userId, @Param("newPwd") String newPwd);
}
