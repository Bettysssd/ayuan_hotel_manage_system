package com.comfort.hotel.service.impl;


import com.comfort.hotel.config.EmailConfig;
import com.comfort.hotel.constants.Constants;
import com.comfort.hotel.domain.dto.SysSettingsDTO;
import com.comfort.hotel.domain.dto.UserLoginDTO;
import com.comfort.hotel.domain.entity.User;
import com.comfort.hotel.mapper.UserMapper;
import com.comfort.hotel.service.UserService;
import com.comfort.hotel.utils.StringTools;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import javax.mail.internet.MimeMessage;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static io.lettuce.core.pubsub.PubSubOutput.Type.message;

/**
 * @Author: ayuan
 * @PS:
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
     * @param user
     */
    @Override
    public void signInUser(User user) {
        // 密码加密
        user.setPassword(StringTools.encodeByMD5(user.getPassword()));
        userMapper.signInUser(user.getUserId(), user.getQqEmail(), user.getPassword());
    }

    /**
     * 用户登录，并获取id
     * @param user
     */
    @Override
    public String loginUserAndGetId(User user) {
        //进行密码校验
        String checkPassword = StringTools.encodeByMD5(user.getPassword());
        // 获取数据库中的密码
        String password = userMapper.selectPasswordByQQAccount(user.getQqEmail());
        // 密码错误
        if (!password.equals(checkPassword)) {
            logger.info("密码错误");
            return null;
        }
        String lastLoginTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //数据库中，查询用户的id
        String userId = userMapper.selectUserIdByQQEmail(user.getQqEmail());
        // 更新当前用户最近一次登录的时间
        userMapper.updateUserLoginTimeByUserId(lastLoginTime, userId);
        return userId;
    }

    // 获取邮箱验证码
    @Override
    public boolean getVerifyCode(UserLoginDTO userLoginDTO)  {
        // 生成验证码
        String verifyCode = StringTools.getRandomString(Constants.LENGTH_5);
        // 判断邮箱号是否正确,能否正常的获取到id
        String userId = userMapper.selectUserIdByQQEmail(userLoginDTO.getQqEmail());
        if (!(userId == null)) {
            // 如果邮箱存在, 将验证码储存在redis中，并且设置过期时间 --5分钟
            redisTemplate.opsForValue().set(Constants.USER_ID + userId, verifyCode, Constants.FIVE, TimeUnit.MINUTES);
            // 发送验证码到邮箱用户
            try {
                sendEmailCode(userLoginDTO.getQqEmail(), verifyCode);
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
        String userId = userMapper.selectUserIdByQQEmail(userLoginDTO.getQqEmail());
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
        String userId = userMapper.selectUserIdByQQEmail(userLoginDTO.getQqEmail());
        if (!(userId == null)) {
            return userMapper.updateUserPasswordById(userId, newPwd).equals(Constants.ONE);
        }
        return false;
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
            helper.setText(String.format(sysSettingsDTO.getRegisterEmailContent(),verifyCode));
            // 设置当前的日期
            helper.setSentDate(new Date());
            // 邮件发送
            javaMailSender.send(message);
        }
    }


