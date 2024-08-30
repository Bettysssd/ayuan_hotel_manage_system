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


    @Insert("insert into user (user_id, qq_email, password) " +
            "values (#{userId}, #{qqAccount}, #{password})")
    void signInUser(@Param("userId") String userId,
                    @Param("qqAccount") String qqAccount,
                    @Param("password") String password);

    @Select("select user_id from user")
    String selectUserIdByQQEmail(String qqEmail);
}
