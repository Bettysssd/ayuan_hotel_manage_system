package com.comfort.hotel.mapper;

import com.comfort.hotel.domain.entity.Supervisor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Author: ayuan
 * @PS:
 * @Date: 2024/9/2 16:17
 */
@Mapper
public interface SupervisorMapper {

    // 根据管理员账号查询管理员id
    @Select("select supervisor_id from supervisor where account = #{account}")
    String getSupervisorIdByAccount(@Param("account") String account);

    // 根据管理员账号查询管理员密码
    @Select("select password from supervisor where account = #{account}")
    String getSupervisorPasswordByAccount(@Param("account") String account);
}
