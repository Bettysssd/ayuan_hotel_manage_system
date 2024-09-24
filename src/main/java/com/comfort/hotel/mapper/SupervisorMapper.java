package com.comfort.hotel.mapper;

import com.comfort.hotel.domain.dto.RoomDTO;
import com.comfort.hotel.domain.entity.Supervisor;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

/**
 * @Author: ayuan
 * @PS: 管理员接口mapper
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

    // 根据管理员id查询管理员姓名
    @Select("select supervisor_name from supervisor where supervisor_id = #{supervisorId}")
    String getSupervisorNameById(@Param("supervisorId") String supervisorId);

}
