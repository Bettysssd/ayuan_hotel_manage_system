package com.comfort.hotel.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.PutObjectRequest;
import com.comfort.hotel.config.OSSConfig;
import com.comfort.hotel.constants.Constants;
import com.comfort.hotel.domain.vo.CarVO;
import com.comfort.hotel.domain.vo.PageVO;
import com.comfort.hotel.mapper.ParkingMapper;
import com.comfort.hotel.mapper.UserMapper;
import com.comfort.hotel.service.ParkingService;
import com.comfort.hotel.service.SupervisorService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;


/**
 * @Author: ayuan
 * @PS:停车接口实现类
 * @Date: 2024/9/16 11:25
 */
@Service
public class ParkingServiceImpl implements ParkingService {

    @Resource
    private ParkingMapper parkingMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private OSSConfig ossConfig;
    private Logger logger = LoggerFactory.getLogger(SupervisorService.class);


    // 删除当前车辆停放信息
    @Override
    public void deleteCurrentCarParkingInfo(String parkingId) {
        parkingMapper.deleteCurrentCarParkingInfo(parkingId);
    }

    // 更新车辆停放信息基本信息
    @Transactional
    @Override
    public synchronized void updateCarParkingInfo(CarVO carVO) {
        // 1.更新车辆停放信息表
        parkingMapper.updateCarParkingInfo(carVO);
        // 2.同步到用户表中去
        // 通过车辆id获取用户表中的原有消费
        Integer costed = userMapper.getUserCost(carVO.getParkingId());
        Integer recentCosted = costed + carVO.getCumulativeCost();
        // 通过车辆id更新用户表当前的消费费用
        userMapper.updateUserPresentCost(recentCosted, carVO.getParkingId());
    }

    // 更新车辆停发图片
    @Override
    public void uploadCarParkingCover(MultipartFile file) {
        // 拼接完整的图片url路径
        String imgUrlPath = Constants.OSS_PATH + file.getOriginalFilename();
        // 判断数据库中是否已经存在，避免重复的上传到阿里云oss上，浪费资源
        int count = parkingMapper.isImgUrlPathExist(imgUrlPath);
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

    // 分页查询车辆停放信息
    @Override
    public PageInfo<CarVO> findCarParkingInList(PageVO pageVO) {
        // 第几页
        Integer page = pageVO.getCurrentPage();
        // 每页显示的大小
        Integer size = pageVO.getSize();
        // 使用pageHelper提高编码效率
        // 默认查询所有记录，PageHelper分页拦截器拦截，即在执行相关Sql之前会拦截自动加上limit
        PageHelper.startPage(page, size);
        List<CarVO> list = parkingMapper.selectParkingCarList(pageVO.getKeyword());
        return new PageInfo<>(list);
    }
}
