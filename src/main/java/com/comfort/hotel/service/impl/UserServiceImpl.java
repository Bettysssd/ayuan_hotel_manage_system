package com.comfort.hotel.service.impl;


import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.PutObjectRequest;
import com.comfort.hotel.config.EmailConfig;
import com.comfort.hotel.config.OSSConfig;
import com.comfort.hotel.constants.Constants;
import com.comfort.hotel.domain.entity.User;
import com.comfort.hotel.domain.qo.ParkingQO;
import com.comfort.hotel.domain.vo.CanteenVO;
import com.comfort.hotel.domain.vo.CarVO;
import com.comfort.hotel.domain.vo.GoodsVO;
import com.comfort.hotel.domain.vo.OrderVO;
import com.comfort.hotel.domain.vo.RoomVO;
import com.comfort.hotel.domain.vo.SysSettingsVO;
import com.comfort.hotel.domain.vo.UserVO;
import com.comfort.hotel.domain.vo.UserLoginVO;
import com.comfort.hotel.domain.vo.UserPayVO;
import com.comfort.hotel.domain.vo.WaiterVO;
import com.comfort.hotel.mapper.CanteenMapper;
import com.comfort.hotel.mapper.GoodsMapper;
import com.comfort.hotel.mapper.OrderMapper;
import com.comfort.hotel.mapper.ParkingMapper;
import com.comfort.hotel.mapper.RoomMapper;
import com.comfort.hotel.mapper.UserMapper;
import com.comfort.hotel.mapper.WaiterMapper;
import com.comfort.hotel.service.OrderService;
import com.comfort.hotel.service.UserService;
import com.comfort.hotel.utils.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeMessage;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
    private OrderMapper orderMapper;
    @Resource
    private RoomMapper roomMapper;
    @Resource
    private WaiterMapper waiterMapper;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private OrderService orderService;
    @Resource
    private EmailConfig emailConfig;
    @Resource
    private OSSConfig ossConfig;
    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private CanteenMapper canteenMapper;
    @Autowired
    private ParkingMapper parkingMapper;

    /**
     * 用户注册
     *
     * @param user
     */
    @Override
    public boolean signInUser(User user) {
        UserVO userVO = new UserVO();
        userVO.setEmail(user.getEmail());
        UserVO originalDTO = userMapper.selectFourKeyField(userVO);
        if (originalDTO == null) {
            // 密码加密
            user.setPassword(StringTools.encodeByMD5(user.getPassword()));
            userMapper.signInUser(user.getUserId(), user.getEmail(), user.getPassword());
            return true;
        } else {
            return false;
        }
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
            logger.error("密码错误");
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
    public boolean getVerifyCode(UserLoginVO userLoginVO) {
        // 生成验证码
        String verifyCode = StringTools.getRandomString(Constants.LENGTH_5);
        // 判断邮箱号是否正确,能否正常的获取到id
        String userId = userMapper.selectUserIdByEmail(userLoginVO.getEmail());
        if (!(userId == null)) {
            // 如果邮箱存在, 将验证码储存在redis中，并且设置过期时间 --5分钟
            redisTemplate.opsForValue().set(Constants.USER_ID + userId, verifyCode, Constants.LENGTH_5, TimeUnit.MINUTES);
            // 发送验证码到邮箱用户
            try {
                sendEmailCode(userLoginVO.getEmail(), verifyCode);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    // 验证是否为当前用户操作
    @Override
    public boolean verify(UserLoginVO userLoginVO) {
        // 获取当前用户id
        String userId = userMapper.selectUserIdByEmail(userLoginVO.getEmail());
        if (!(userId == null)) {
            // 从redis中获取对应的验证码，并进行比对
            return userLoginVO.getVerifyCode().equals(redisTemplate.opsForValue().get(Constants.USER_ID + userId));
        }
        return false;
    }

    // 为当前用户重置密码
    @Override
    public boolean resetPwd(UserLoginVO userLoginVO) {
        String newPwd = StringTools.encodeByMD5(userLoginVO.getPassword());
        // 获取当前用户id
        String userId = userMapper.selectUserIdByEmail(userLoginVO.getEmail());
        if (!(userId == null)) {
            return userMapper.updateUserPasswordById(userId, newPwd).equals(Constants.ONE);
        }
        return false;
    }

    // 用户登录
    @Override
    public void login(HttpServletResponse response, UserLoginVO userLoginVO) {
        User user = new User();
        BeanUtils.copyProperties(userLoginVO, user);
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
            response.setHeader(Constants.USER_ID, userId);
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
    public List<UserPayVO> getUserDifferentPaySituation(Integer status) {
        return userMapper.getUserDifferentPaySituation(status);
    }

    // 根据id获取用户头像
    @Override
    public String getUserAvatar(String userId) {
        return userMapper.getUserAvatarById(userId);
    }

    // 获取用户基本信息
    @Override
    public UserVO getUserInfo(String userId) {
        return userMapper.getUserInfoById(userId);
    }

    // 更新用户图片
    @Override
    public void uploadUserCover(MultipartFile file) {
        // 拼接完整的图片url路径
        String imgUrlPath = Constants.OSS_PATH + file.getOriginalFilename();
        // 判断数据库中是否已经存在，避免重复的上传到阿里云oss上，浪费资源
        int count = userMapper.isImgUrlPathExist(imgUrlPath);
        if (count == Constants.ZERO) {
            // 获取Oss客户端服务对象实例
            OSS ossClient = new OSSClientBuilder().build(ossConfig.getEndPoint(), ossConfig.getAccessKeyId()
                    , ossConfig.getAccessKeySecret());
            try {
                // 上传文件
                ossClient.putObject(new PutObjectRequest(ossConfig.getBucketName(), file.getOriginalFilename(), file.getInputStream()));
                //  设置对象ACL为公共读
                ossClient.setObjectAcl(ossConfig.getBucketName(), file.getOriginalFilename(), CannedAccessControlList.PublicRead);
                // 关闭OSSClient。
                ossClient.shutdown();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    // 更新用户信息
    @Override
    public Integer updateUserInfo(UserVO userVO) {
        UserVO checkedUserVO = new UserVO();
        // 判断是否为当前用户
        UserVO dto1 = null;
        // 判断用户的微信账号是否为空
        checkedUserVO.setWechatAccount(userVO.getWechatAccount());
        dto1 = userMapper.selectFourKeyField(checkedUserVO);
        if (dto1 != null) {
            if (dto1.getUserId().equals(userVO.getUserId())) {
                if (!dto1.getWechatAccount().equals(userVO.getWechatAccount())) {
                    userMapper.updateUserInfo(userVO);
                }
            } else {
                return 3;
            }
        } else {
            userMapper.updateUserInfo(userVO);
        }
        checkedUserVO.setWechatAccount(null);
        // 判断用户的手机号码是否为空
        checkedUserVO.setPhoneNumber(userVO.getPhoneNumber());
        dto1 = userMapper.selectFourKeyField(checkedUserVO);
        if (dto1 != null) {
            if (dto1.getUserId().equals(userVO.getUserId())) {
                if (!dto1.getPhoneNumber().equals(userVO.getPhoneNumber())) {
                    userMapper.updateUserInfo(userVO);
                }
            } else {
                return 2;
            }
        } else {
            userMapper.updateUserInfo(userVO);
        }
        checkedUserVO.setPhoneNumber(null);
        // 判断用户的身份证号码是否为空
        checkedUserVO.setIdCardCode(userVO.getIdCardCode());
        dto1 = userMapper.selectFourKeyField(checkedUserVO);
        if (dto1 != null) {
            if (dto1.getUserId().equals(userVO.getUserId())) {
                if (!dto1.getIdCardCode().equals(userVO.getIdCardCode())) {
                    userMapper.updateUserInfo(userVO);
                }
            } else {
                return 1;
            }
        } else {
            userMapper.updateUserInfo(userVO);
        }
        return 0;
    }

    // 查看用户是否已经填完必填的个人信息
    @Override
    public boolean checkUserInfoAlreadyFinished(String userId) {
        UserVO userVO = userMapper.checkUserInfoAlreadyFinished(userId);
        // 判断用户的必要信息是否为空
        try {
            return !userVO.getUserName().isEmpty() && !userVO.getIdCardCode().isEmpty()
                    && !userVO.getPhoneNumber().isEmpty() && !userVO.getWechatAccount().isEmpty();
        } catch (NullPointerException e) {
            return false;
        }

    }

    // 酒店客房预定
    @Override
    @Transactional
    public boolean bookHotel(String roomId, String userId, Integer count, Float price, String roomName) {
        // 判断当前用户是否已经预定过酒店
        String roomIdInUser = userMapper.selectRoomIdByUserId(userId);
        if (roomIdInUser == null) {
            RoomVO roomVO = new RoomVO();
            UserVO userVO = new UserVO();
            OrderVO orderVO = new OrderVO();
            WaiterVO waiterVO = new WaiterVO();
            // 查询符合条件的服务员id集合
            List<String> strings = waiterMapper.selectWantedWaiter(Constants.ZERO, Constants.ZERO);
            // 随机获取某个服务员id
            String waiterId = strings.get(new Random().nextInt(strings.size()));
            orderVO.setWaiterId(waiterId);
            // 更新当前入住的人数
            roomVO.setCurrentMarkPeople(count + Constants.ONE);
            // 更新当前客房的状态
            roomVO.setRoomStatus(Constants.ONE);
            // 设置roomId
            roomVO.setRoomId(roomId);
            orderVO.setObjectId(roomId);
            // 设置房间id
            userVO.setRoomId(roomId);
            // 设置用户id
            userVO.setUserId(userId);
            orderVO.setUserId(userId);
            orderVO.setOrderSpend(price);
            orderVO.setObjectStatus(Constants.ZERO);
            orderVO.setOrderInfo(Constants.WAIT_TO_PAY);
            orderVO.setOrderId(StringTools.getRandomString(Constants.LENGTH_8));
            orderVO.setObjectName(roomName);
            waiterVO.setWaiterStatus(Constants.ONE);
            waiterVO.setWaiterId(waiterId);
            // 更新信息
            roomMapper.updateRoomInfo(roomVO);
            userMapper.updateUserInfo(userVO);
            orderMapper.addNewOrderInfo(orderVO);
            waiterMapper.updateWaiterInfo(waiterVO);
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(() -> executeAfterBooking(userId, orderVO.getOrderId(),
                    orderVO.getObjectId(), orderVO.getObjectStatus()), Constants.FIFTEEN_MIN, TimeUnit.MINUTES);
            return true;
        }
        return false;
    }

    // 订单定时任务检查

    private void executeAfterBooking(String userId, String orderId, String objectId, Integer objectStatus) {
        System.out.println("开始执行定时任务，订单编号为：" + orderId);
            // 查询当前订单的状态 -> 为0时，说明订单没有被支付，则自动取消订单
            if (orderMapper.selectCurrentOrderStatus(orderId).equals(Constants.ZERO)) {
                orderService.cancelCurrentOrder(userId, orderId, objectId, objectStatus, Constants.ONE, true);
            }
        System.out.println("执行15分钟后的定时任务完成");
    }

    // 酒店客房合租
    @Override
    @Transactional
    public Integer rentalTogether(String idCode, String userId, Integer count, String roomId) {

        // 判断当前用户是否已经预定过或合租过酒店客房
        String roomIdInUser = userMapper.selectRoomIdByUserId(userId);
        if (roomIdInUser == null) {
            // 判断合租人是否已经预定酒店客房
            String roomIdInRental = userMapper.selectRoomIdByUserIdCardCode(idCode);
            if (roomIdInRental != null) {
                // 判断用户是否已经下单了，如果不是下单则直接返回
                Integer isPaid = orderMapper.selectOrderStatus(roomIdInRental);
                if (isPaid != null) {
                    if (!orderMapper.selectOrderStatus(roomIdInRental).equals(Constants.ONE)) {
                        return Constants.FOUR;
                    }
                }
                // 判断合租人的酒店与当前用户所选择的酒店是否一致
                if (roomId.equals(roomIdInRental)) {
                    // 合租成功，已成功录入信息
                    UserVO userVO = new UserVO();
                    RoomVO roomVO = new RoomVO();
                    // 设置用户id
                    userVO.setUserId(userId);
                    // 设置房间id
                    userVO.setRoomId(roomIdInRental);
                    // 更新当前入住的人数
                    // 设置roomId
                    roomVO.setRoomId(roomIdInRental);
                    roomVO.setCurrentMarkPeople(count + Constants.ONE);
                    userMapper.updateUserInfo(userVO);
                    roomMapper.updateRoomInfo(roomVO);
                    return Constants.THREE;
                } else {
                    // 当前合租人预定的房间与您选择合租的房间不一致
                    return Constants.TWO;
                }
            } else {
                // 当前合租人没有预定客房或当前合租人不存在
                return Constants.ONE;
            }
        } else {
            // 当前用户已经预定或合租过酒店了，不能再进行合租操作
            return Constants.ZERO;
        }
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
        SysSettingsVO sysSettingsVO = new SysSettingsVO();
        // 设置验证码的头部
        helper.setSubject(sysSettingsVO.getRegisterEmailTitle());
        // 设置邮件发送主体，将验证码写入到文本格式中去
        helper.setText(String.format(sysSettingsVO.getRegisterEmailContent(), verifyCode));
        // 设置当前的日期
        helper.setSentDate(new Date());
        // 邮件发送
        javaMailSender.send(message);
    }

    // 商品下单
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String bookGoods(String goodsId, String userId, Integer currentSelectedGoodsAmount, Float price) {
        try {
            GoodsVO goodsVO = new GoodsVO();
            OrderVO orderVO = new OrderVO();
            // 设置用户id
            orderVO.setUserId(userId);
            // 设置商品id
            goodsVO.setGoodsId(goodsId);
            orderVO.setObjectId(goodsId);
            // 设置商品剩余数量
            Integer storedAmount = goodsMapper.selectGoodsTotalAmountById(goodsId);
            if (storedAmount > currentSelectedGoodsAmount) {
                goodsVO.setGoodsAmount(goodsMapper.selectGoodsTotalAmountById(goodsId) - currentSelectedGoodsAmount);
            }
            // 如果商品数量刚好为0，说明此时商品已经售完
            else if (storedAmount.equals(currentSelectedGoodsAmount)) {
                goodsVO.setGoodsStatus(Constants.ONE);
                // 更新商品状态 -- 设置缺货
                goodsMapper.updateGoodsInfo(goodsVO);
            } else {
                throw new RuntimeException("商品数量不足，请重新选择购买数量");
            }
            // 设置订单id
            orderVO.setOrderId(StringTools.getRandomString(Constants.LENGTH_8));
            // 设置商品价格
            orderVO.setOrderSpend(price * currentSelectedGoodsAmount);
            // 设置商品名称
            orderVO.setObjectName(goodsMapper.selectGoodsNameById(goodsId));
            // 设置商品购买数量
            orderVO.setOrderAmount(currentSelectedGoodsAmount);
            // 设置商品属性
            orderVO.setObjectStatus(Constants.THREE);
            // 查询符合条件的服务员id集合
            List<String> strings = waiterMapper.selectWantedWaiter(Constants.ZERO, Constants.ONE);
            // 随机获取某个服务员id
            String waiterId = strings.get(new Random().nextInt(strings.size()));
            orderVO.setWaiterId(waiterId);
            // 更新当前表单
            orderMapper.addNewOrderInfo(orderVO);
            goodsMapper.updateGoodsInfo(goodsVO);
            // 设置定时任务
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(() -> executeAfterBooking(userId, orderVO.getOrderId(),
                    orderVO.getObjectId(), orderVO.getObjectStatus()), Constants.FIFTEEN_MIN, TimeUnit.MINUTES);
            return "success";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // 餐馆下单
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String bookCanteen(String canteenId, String userId, Integer currentSelectedPersonAmount) {
        try {
            String orderId = orderMapper.selectOrderByMoreCondition(userId, Constants.ONE);
            // 为空, 说明当前用户没有下了关于酒店餐厅的定单
            if (orderId == null) {
                OrderVO orderVO = new OrderVO();
                CanteenVO canteenVO = new CanteenVO();
                // 设置订单id
                orderVO.setOrderId(StringTools.getRandomString(Constants.LENGTH_8));
                // 设置用户id
                orderVO.setUserId(userId);
                // 设置业务id
                orderVO.setObjectId(canteenId);
                canteenVO.setCanteenId(canteenId);
                // 设置当前业务的类型
                orderVO.setOrderStatus(Constants.ZERO);
                // 设置当前订单中的商品类型
                orderVO.setObjectStatus(Constants.ONE);
                // 设置消费对象名称
                orderVO.setObjectName(canteenMapper.selectCanteenNameById(canteenId));
                // 设置当前总的就餐人数,如果满人则更新状态
                Integer tokenTables = canteenMapper.selectCanteenNumberOfDinersById(canteenId) + currentSelectedPersonAmount;
                if (canteenMapper.selectCanteenTotalTables(canteenId).equals(tokenTables)) {
                    // 将餐厅的位置状态进行更新为无空余位置
                    canteenVO.setCanteenTableStatus(Constants.ONE);
                }
                canteenVO.setCanteenNumberOfDiners(tokenTables);
                // 设置当前人数到订单表
                orderVO.setOrderAmount(currentSelectedPersonAmount);
                orderMapper.addNewOrderInfo(orderVO);
                canteenMapper.updateCanteenInfo(canteenVO);
                // 设置定时任务
                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.schedule(() -> executeAfterBooking(userId, orderVO.getOrderId(),
                        orderVO.getObjectId(), orderVO.getObjectStatus()), Constants.ONE_HUNDRED_AND_FIFTY_MIN, TimeUnit.MINUTES);
            }
            // 不为空, 说明当前用户已经下了关于酒店餐厅的定单
            else {
                return "餐馆下单失败, 当前用户有仍未支付的餐厅订单,订单编号为:" + orderId;
            }

        } catch (Exception e) {
            return "餐馆下单失败, 原因为:" + e.getMessage();
        }
        return "1";
    }

    // 预定泊车业务
    @Override
    @Transactional
    public String bookParking(ParkingQO parkingQO) {
        try {
            CarVO carVO = new CarVO();
            OrderVO orderVO = new OrderVO();
            WaiterVO waiterVO = new WaiterVO();
            String userId = parkingQO.getUserId();
            String parkingId = StringTools.getRandomString(Constants.LENGTH_8);
            carVO.setCarCover(parkingQO.getCarCover());
            carVO.setCarNumber(parkingQO.getCarNumber());
            carVO.setCarName(parkingQO.getCarName());
            carVO.setParkingId(parkingId);
            carVO.setLastParkingTime(LocalDateTime.now());
            carVO.setCarStatus(Constants.ONE);
            orderVO.setOrderId(StringTools.getRandomString(Constants.LENGTH_8));
            orderVO.setUserId(userId);
            orderVO.setObjectId(parkingId);
            // 查询符合条件的服务员id集合
            List<String> strings = waiterMapper.selectWantedWaiter(Constants.ZERO, Constants.TWO);
            // 随机获取某个服务员id
            String waiterId = strings.get(new Random().nextInt(strings.size()));
            orderVO.setWaiterId(waiterId);
            orderVO.setObjectName("车辆泊车服务");
            orderVO.setObjectStatus(Constants.TWO);
            orderVO.setOrderStatus(Constants.ZERO);
            orderVO.setOrderInfo(Constants.WAIT_TO_PAY);
            // 设置当前服务员的工作状态
            waiterVO.setWaiterStatus(Constants.ONE);
            waiterVO.setWaiterId(waiterId);
            // 模拟此时泊车员进行泊车的操作, 仅此而已
            waiterVO.setWaiterId(orderVO.getWaiterId());
            waiterVO.setWaiterStatus(Constants.ZERO);
            waiterMapper.updateWaiterInfo(waiterVO);
            parkingMapper.insertCarInfo(carVO);
            orderMapper.addNewOrderInfo(orderVO);
            return "1";
        } catch (Exception e) {
            throw new RuntimeException("预定泊车失败, 原因为:" + e.getMessage());
        }
    }
}


