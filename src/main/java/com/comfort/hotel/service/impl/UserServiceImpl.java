package com.comfort.hotel.service.impl;


import com.comfort.hotel.config.EmailConfig;
import com.comfort.hotel.constants.Constants;
import com.comfort.hotel.domain.dto.SysSettingsDTO;
import com.comfort.hotel.domain.dto.UserLoginDTO;
import com.comfort.hotel.domain.dto.UserPayDTO;
import com.comfort.hotel.domain.entity.User;
import com.comfort.hotel.mapper.UserMapper;
import com.comfort.hotel.service.UserService;
import com.comfort.hotel.utils.StringTools;
import org.springframework.beans.BeanUtils;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;



/**
 * @Author: ayuan
 * @PS: 用户管理接口实现类
 * @Date: 2024/8/29 17:48
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private EmailConfig emailConfig;

    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    /**
     * 用户注册
     *
     * @param user
     */
    @Override
    public void signInUser(User user) {
        // 密码加密
        user.setPassword(StringTools.encodeByMD5(user.getPassword()));
        userMapper.signInUser(user.getUserId(), user.getEmail(), user.getPassword());
    }

    /**
     * 用户登录，并获取id
     *
     * @param user
     */
    private String getUserId(User user) {
        //进行密码校验
        String checkPassword = StringTools.encodeByMD5(user.getPassword());
        // 获取数据库中的密码
        String password = userMapper.selectPasswordByAccount(user.getEmail());
        // 密码错误或数据库中没有该用户的密码
        if (password == null || !password.equals(checkPassword)) {
            logger.info("密码错误");
            return null;
        }
        String lastLoginTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //数据库中，查询用户的id
        String userId = userMapper.selectUserIdByEmail(user.getEmail());
        // 更新当前用户最近一次登录的时间
        userMapper.updateUserLoginTimeByUserId(lastLoginTime, userId);
        return userId;
    }




    // 获取邮箱验证码
    @Override
    public boolean getVerifyCode(UserLoginDTO userLoginDTO) {
        // 生成验证码
        String verifyCode = StringTools.getRandomString(Constants.LENGTH_5);
        // 判断邮箱号是否正确,能否正常的获取到id
        String userId = userMapper.selectUserIdByEmail(userLoginDTO.getEmail());
        if (!(userId == null)) {
            // 如果邮箱存在, 将验证码储存在redis中，并且设置过期时间 --5分钟
            redisTemplate.opsForValue().set(Constants.USER_ID + userId, verifyCode, Constants.LENGTH_5, TimeUnit.MINUTES);
            // 发送验证码到邮箱用户
            try {
                sendEmailCode(userLoginDTO.getEmail(), verifyCode);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    // 验证是否为当前用户操作
    @Override
    public boolean verify(UserLoginDTO userLoginDTO) {
        // 获取当前用户id
        String userId = userMapper.selectUserIdByEmail(userLoginDTO.getEmail());
        if (!(userId == null)) {
            // 从redis中获取对应的验证码，并进行比对
            return userLoginDTO.getVerifyCode().equals(redisTemplate.opsForValue().get(Constants.USER_ID + userId));
        }
        return false;
    }

    // 为当前用户重置密码
    @Override
    public boolean resetPwd(UserLoginDTO userLoginDTO) {
        String newPwd = StringTools.encodeByMD5(userLoginDTO.getPassword());
        // 获取当前用户id
        String userId = userMapper.selectUserIdByEmail(userLoginDTO.getEmail());
        if (!(userId == null)) {
            return userMapper.updateUserPasswordById(userId, newPwd).equals(Constants.ONE);
        }
        return false;
    }

    // 用户登录
    @Override
    public void login(HttpServletResponse response, UserLoginDTO userLoginDTO) {
        User user = new User();
        BeanUtils.copyProperties(userLoginDTO, user);
        // 查找数据库，查看用户是否已经注册,获取密码匹配错误的
        String userId = getUserId(user);
        String userNameInBase64 = StringTools.encodeUserNameToBase64(getUserName(userId));
        if (userId != null) {
            // 不为空，说明当前用户已经注册，可以为当前用户设置token
            String token = UUID.randomUUID().toString();
            // 将token存储在redis中，并且设置过期时间为1天
            redisTemplate.opsForValue().set(Constants.USER_TOKEN, token, Constants.ONE, TimeUnit.DAYS);
            // 将 userId、userName和token分别存储到response中， 方便以后使用
            response.setHeader("Content-Type", "application/json; charset=utf-8");
            response.setHeader(Constants.USER_TOKEN, token);
            response.setHeader(Constants.USER_NAME, userNameInBase64);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    // 获取用户名字
    private String getUserName(String userId) {
       return userMapper.getUserName(userId);
    }

    // 用户登出
    @Override
    public void logout() {
        // redis同步进行删除token
        redisTemplate.delete(Constants.USER_TOKEN);
    }

    // 通过用户特征分类用户
    @Override
    public Integer distinguishUserByCondition(Integer type, Integer status) {
       return userMapper.distinguishUserByCondition(type, status);
    }

    // 异步获取不同状态下的用户消费情况
    @Override
    public List<UserPayDTO> getUserDifferentPaySituation(Integer status) {
        return userMapper.getUserDifferentPaySituation(status);
    }

    // 发送验证码
    private void sendEmailCode(String email, String verifyCode) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        // 邮件发送者
        helper.setFrom(emailConfig.getSendUserName());
        // 邮件接受方
        helper.setTo(email);
        // 获取邮件发送模板
        SysSettingsDTO sysSettingsDTO = new SysSettingsDTO();
        // 设置验证码的头部
        helper.setSubject(sysSettingsDTO.getRegisterEmailTitle());
        // 设置邮件发送主体，将验证码写入到文本格式中去
        helper.setText(String.format(sysSettingsDTO.getRegisterEmailContent(), verifyCode));
        // 设置当前的日期
        helper.setSentDate(new Date());
        // 邮件发送
        javaMailSender.send(message);
    }
}


