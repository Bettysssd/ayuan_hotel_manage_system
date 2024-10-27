package com.comfort.hotel.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.PutObjectRequest;
import com.comfort.hotel.config.OSSConfig;
import com.comfort.hotel.constants.Constants;
import com.comfort.hotel.domain.dto.CanteenDTO;
import com.comfort.hotel.domain.dto.GoodsDTO;
import com.comfort.hotel.domain.dto.RoomDTO;
import com.comfort.hotel.domain.dto.WaiterDTO;
import com.comfort.hotel.domain.vo.CanteenVO;
import com.comfort.hotel.domain.vo.GoodsVO;
import com.comfort.hotel.domain.vo.OrderVO;
import com.comfort.hotel.domain.vo.PageVO;
import com.comfort.hotel.domain.vo.ReviewVO;
import com.comfort.hotel.domain.vo.RoomVO;
import com.comfort.hotel.domain.vo.WaiterVO;
import com.comfort.hotel.mapper.CanteenMapper;
import com.comfort.hotel.mapper.GoodsMapper;
import com.comfort.hotel.mapper.OrderMapper;
import com.comfort.hotel.mapper.RoomMapper;
import com.comfort.hotel.mapper.WaiterMapper;
import com.comfort.hotel.service.SupervisorService;
import com.comfort.hotel.service.WaiterService;
import com.comfort.hotel.utils.StringTools;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * @Author: ayuan
 * @PS: 服务员管理接口实现类
 * @Date: 2024/9/2 16:12
 */
@Service
public class WaiterServiceImpl implements WaiterService {

    @Resource
    private WaiterMapper waiterMapper;
    @Resource
    private OSSConfig ossConfig;
    private Logger logger = LoggerFactory.getLogger(SupervisorService.class);
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private CanteenMapper canteenMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private OrderMapper orderMapper;


    //分页查询服务员信息
    @Override
    public PageInfo<WaiterVO> findWaiterInList(PageVO pageVO) {
        // 第几页
        Integer page = pageVO.getCurrentPage();
        // 每页显示的大小
        Integer size = pageVO.getSize();
        // 使用pageHelper提高编码效率
        // 默认查询所有记录，PageHelper分页拦截器拦截，即在执行相关Sql之前会拦截自动加上limit
        PageHelper.startPage(page, size);
        List<WaiterVO> list = waiterMapper.selectWaiterList(pageVO.getKeyword(), pageVO.getJobTypes());
        return new PageInfo<>(list);
    }

    // 根据工作类型查询不同的管理员人数
    @Override
    public Integer getWaiterCountByType(Integer[] types) {
        return waiterMapper.getWaiterCountByType(types);
    }

    // 根据状态查询不同状态的泊车员人数
    @Override
    public Integer getParkingByStatus(Integer status) {
        return waiterMapper.getParkingByStatus(status);
    }

    // 查看当前服务员信息
    @Override
    public ReviewVO showCurrentWaiterAndGoods(String waiterId, String objectId, Integer objectStatus) {
        WaiterDTO waiterDTO = waiterMapper.selectWaiterById(waiterId);
        ReviewVO reviewVO = new ReviewVO();
        switch (objectStatus) {
            case 0:
                RoomDTO roomDTO = roomMapper.selectRoomById(objectId);
                switch (roomDTO.getRoomType()) {
                    case 1:
                        reviewVO.setGoodsName("单人间");
                        break;
                    case 2:
                        reviewVO.setGoodsName("经济双人间");
                        break;
                    case 3:
                        reviewVO.setGoodsName("豪华单人间");
                        break;
                    case 4:
                        reviewVO.setGoodsName("豪华双人间");
                        break;
                    default:
                        reviewVO.setGoodsName("家庭间");
                        break;
                }
                reviewVO.setGoodsCover(roomDTO.getRoomCover());
                reviewVO.setGoodsPositiveRating(roomDTO.getRoomRating());
                break;
            case 1:
                CanteenDTO canteenDTO = canteenMapper.selectCanteenById(objectId);
                reviewVO.setGoodsCover(canteenDTO.getCanteenCover());
                reviewVO.setGoodsName(canteenDTO.getCanteenName());
                reviewVO.setGoodsPositiveRating(canteenDTO.getCanteenRating());
                break;
            case 3:
                GoodsDTO goodsDTO = goodsMapper.selectGoodsById(objectId);
                reviewVO.setGoodsCover(goodsDTO.getGoodsCover());
                reviewVO.setGoodsName(goodsDTO.getGoodsName());
                reviewVO.setGoodsPositiveRating(goodsDTO.getPositiveRating());
                break;

        }
        if (waiterDTO != null) {
            reviewVO.setWaiterCover(waiterDTO.getWaiterCover());
            reviewVO.setWaiterName(waiterDTO.getWaiterName());
            reviewVO.setWaiterPositiveRating(waiterDTO.getWaiterPositiveRating());
            switch (waiterDTO.getJobType()) {
                case 0:
                    reviewVO.setWaiterJobType("保洁人员");
                    break;
                case 1:
                    reviewVO.setWaiterJobType("商品配送员");
                    break;
                case 2:
                    reviewVO.setWaiterJobType("泊车员");
                    break;
            }
        }
        return reviewVO;
    }

    // 评价当前服务员和商品
    @Override
    @Transactional
    public boolean reviewCurrentWaiterAndGoods(String waiterId, String objectId,
                                               String orderId, String userId,
                                               Integer objectStatus, Integer status) {
        // 给予不同了类型商品 好评差评
        switch (objectStatus) {
            case 0:
                RoomVO roomVO = new RoomVO();
                roomVO.setRoomId(objectId);
                int roomTrumpUp = roomMapper.selectTrumpUp(objectId);
                int roomTrumpDown = roomMapper.selectTrumpDown(objectId);
                int roomTotal = roomTrumpDown + roomTrumpUp;
                if (status == 1) {
                    roomTrumpUp += Constants.ONE;
                    roomVO.setRoomTrumpUp(roomTrumpUp);
                    roomVO.setRoomRating(calculateRatio(roomTrumpUp, roomTotal == 0 ? 1 : roomTotal));
                } else {
                    roomTrumpDown += Constants.ONE;
                    roomVO.setRoomTrumpDown(roomTrumpDown);
                    roomVO.setRoomRating(calculateRatio(roomTrumpUp, roomTotal == 0 ? 1 : roomTotal + 1));
                }
                if (roomMapper.updateRoomInfo(roomVO) == 0) {
                    return false;
                }
                ;
                break;
            case 1:
                CanteenVO canteenVO = new CanteenVO();
                canteenVO.setCanteenId(objectId);
                int canteenTrumpUp = canteenMapper.selectTrumpUp(objectId);
                int canteenTrumpDown = canteenMapper.selectTrumpDown(objectId);
                int canteenTotal = canteenTrumpDown + canteenTrumpUp;
                if (status == 1) {
                    canteenTrumpUp += Constants.ONE;
                    canteenVO.setCanteenTrumpUp(canteenTrumpUp);
                    canteenVO.setCanteenRating(calculateRatio(canteenTrumpUp, canteenTrumpUp == 0 ? 1 : canteenTrumpUp));
                } else {
                    canteenTrumpDown += Constants.ONE;
                    canteenVO.setCanteenTrumpDown(canteenTrumpDown);
                    canteenVO.setCanteenRating(calculateRatio(canteenTrumpUp, canteenTrumpUp == 0 ? 1 : canteenTrumpUp + 1));
                }
                if (canteenMapper.updateCanteenInfo(canteenVO) == 0) {
                    return false;
                }
                ;
                break;
            case 3:
                GoodsVO goodsVO = new GoodsVO();
                goodsVO.setGoodsId(objectId);
                int goodsTrumpUp = goodsMapper.selectTrumpUp(objectId);
                int goodsTrumpDown = goodsMapper.selectTrumpDown(objectId);
                int goodsTotal = goodsTrumpUp + goodsTrumpDown;
                if (status == 1) {
                    goodsTrumpUp += Constants.ONE;
                    goodsVO.setGoodsTrumpUp(goodsTrumpUp);
                    goodsVO.setPositiveRating(calculateRatio(goodsTrumpUp, goodsTrumpUp == 0 ? 1 : goodsTrumpUp));
                } else {
                    goodsTrumpDown += Constants.ONE;
                    goodsVO.setGoodsTrumpDown(goodsTrumpDown);
                    goodsVO.setPositiveRating(calculateRatio(goodsTrumpUp, goodsTrumpUp == 0 ? 1 : goodsTrumpUp + 1));
                }
                if (goodsMapper.updateGoodsInfo(goodsVO) == 0) {
                    return false;
                }
                ;
                break;
        }

        // 服务员好评、差评, 除餐厅外
        WaiterVO waiterVO = new WaiterVO();
        waiterVO.setWaiterId(waiterId);
        int waiterTrumpUp = waiterMapper.selectTrumpUp(waiterId);
        int waiterTrumpDown = waiterMapper.selectTrumpDown(waiterId);
        int waiterTotal = waiterTrumpDown + waiterTrumpUp;
        if (status == 1) {
            waiterTrumpUp += Constants.ONE;
            waiterVO.setWaiterTrumpUp(waiterTrumpUp);
            waiterVO.setWaiterPositiveRating(BigDecimal.valueOf((waiterTrumpUp) / (waiterTotal == 0 ? 1 : waiterTotal)));
        } else {
            waiterTrumpDown += Constants.ONE;
            waiterVO.setWaiterTrumpDown(waiterTrumpDown);
            waiterVO.setWaiterPositiveRating(BigDecimal.valueOf((waiterTrumpUp) / (waiterTotal == 0 ? 1 : waiterTotal + 1)));
        }
        if (waiterMapper.updateWaiterInfo(waiterVO) == 0) {
            return false;
        }
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(orderId);
        orderVO.setUserId(userId);
        // 设置为已评价
        orderVO.setOrderStatus(Constants.THREE);
        if (orderMapper.updateCurrentOrder(orderVO) == 0) {
            return false;
        }
        return true;
    }

    // 更新服务员图片
    @Override
    public void uploadWaiterCover(MultipartFile file) {
        // 拼接完整的图片url路径
        String imgUrlPath = Constants.OSS_PATH + file.getOriginalFilename();
        // 判断数据库中是否已经存在，避免重复的上传到阿里云oss上，浪费资源
        int count = waiterMapper.isImgUrlPathExist(imgUrlPath);
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

    // 更新服务员基本信息
    @Override
    public void updateWaiterInfo(WaiterVO waiterVO) {
        Date date = new Date();

        //判断服务员是否修改的状态
        // 设置服务员上班时间
        if (Constants.ONE.equals(waiterVO.getWaiterStatus())) {
            waiterVO.setLastOfficeTime(date);
        }
        // 设置服务员休假时间
        if (Constants.TWO.equals(waiterVO.getWaiterStatus())) {
            waiterVO.setLastDayOffTime(date);
        }
        // 设置服务员离职时间
        if (Constants.THREE.equals(waiterVO.getWaiterStatus())) {
            waiterVO.setLastFireTime(date);
        }
        waiterMapper.updateWaiterInfo(waiterVO);
    }

    // 删除当前服务员
    @Override
    public void deleteCurrentWaiter(String waiterId) {
        waiterMapper.deleteWaiterById(waiterId);
    }


    // 录入服务员信息
    @Override
    public void addWaiterInfo(WaiterVO waiterVO) {
        // 随机生成roomId
        waiterVO.setWaiterId(StringTools.getRandomString(Constants.LENGTH_8));
        waiterMapper.addWaiterInfo(waiterVO);
    }

    public static BigDecimal calculateRatio(int goodsTrumpUp, int goodsTotal) {
        BigDecimal numerator = BigDecimal.valueOf(goodsTrumpUp);
        BigDecimal denominator = BigDecimal.valueOf(goodsTotal == 0 ? 1 : goodsTotal + 1);

        // 进行除法运算并保留两位小数
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }
}
