package com.comfort.hotel.service.impl;

import com.comfort.hotel.constants.Constants;
import com.comfort.hotel.domain.vo.PageVO;
import com.comfort.hotel.domain.vo.SuperVisorLoginVO;
import com.comfort.hotel.domain.vo.UserVO;
import com.comfort.hotel.domain.entity.Supervisor;
import com.comfort.hotel.mapper.SupervisorMapper;
import com.comfort.hotel.mapper.UserMapper;
import com.comfort.hotel.service.SupervisorService;
import com.comfort.hotel.utils.StringTools;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author: ayuan
 * @PS: 管理员接口实现类
 * @Date: 2024/9/2 16:12
 */
@Service
public class SupervisorServiceImpl implements SupervisorService {

    @Resource
    private SupervisorMapper supervisorMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate<String, String> redisTemplate;


    // 获取管理员id
    public String getSupervisorId(Supervisor supervisor) {
        // 获取数据库密码
        String password = supervisorMapper.getSupervisorPasswordByAccount(supervisor.getAccount());
        // 判断登录密码是否一致
        if (password != null && password.equals(StringTools.encodeByMD5(supervisor.getPassword()))) {
            return supervisorMapper.getSupervisorIdByAccount(supervisor.getAccount());
        } else {
            return null;
        }
    }

    // 管理员登录
    @Override
    public void login(HttpServletResponse response, SuperVisorLoginVO superVisorLoginVO) {
        Supervisor supervisor = new Supervisor();
        BeanUtils.copyProperties(superVisorLoginVO, supervisor);
        // 查找数据库，查看用户是否已经注册,获取密码匹配错误的
        String supervisorId = getSupervisorId(supervisor);
        String userNameInBase64 = StringTools.encodeUserNameToBase64(getSupervisorNameById(supervisorId));
        if (supervisorId != null) {
            String token = UUID.randomUUID().toString();
            // 将token存储在redis中，并且设置过期时间为1天
            redisTemplate.opsForValue().set(Constants.SUPERVISOR_TOKEN, token, Constants.ONE, TimeUnit.DAYS);
            // 将supervisorName和token分别存储到response中， 方便以后使用
            response.setHeader(Constants.SUPERVISOR_NAME, userNameInBase64);
            response.setHeader(Constants.SUPERVISOR_TOKEN, token);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    // 通过id获取管理员的名字
    private String getSupervisorNameById(String supervisorId) {
        return supervisorMapper.getSupervisorNameById(supervisorId);
    }

    // 管理员登出
    @Override
    public void logout() {
        redisTemplate.delete(Constants.SUPERVISOR_TOKEN);
    }

    @Override
    public PageInfo<UserVO> findUserInList(PageVO pageVO) {
        // 第几页
        Integer page = pageVO.getCurrentPage();
        // 每页显示的大小
        Integer size = pageVO.getSize();
        // 使用pageHelper提高编码效率
        // 默认查询所有记录，PageHelper分页拦截器拦截，即在执行相关Sql之前会拦截自动加上limit
        PageHelper.startPage(page, size);
        List<UserVO> list = userMapper.selectUserList(pageVO.getKeyword());
        return new PageInfo<>(list);
    }

    // 修改用户当前状态
    @Override
    public void changeUserStatus(UserVO userVO) {
        userMapper.changeUserStatusById(userVO.getUserStatus(), userVO.getUserId());
    }


    // 恢复默认用户头像
    @Override
    public void recoverDefaultImage(String imgUrl, String userId) {
        userMapper.recoverDefaultImage(imgUrl, userId);
    }


}
