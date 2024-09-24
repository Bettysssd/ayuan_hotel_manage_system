package com.comfort.hotel.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.PutObjectRequest;
import com.comfort.hotel.config.OSSConfig;
import com.comfort.hotel.constants.Constants;
import com.comfort.hotel.domain.dto.PageDTO;
import com.comfort.hotel.domain.dto.WaiterDTO;
import com.comfort.hotel.mapper.WaiterMapper;
import com.comfort.hotel.service.SupervisorService;
import com.comfort.hotel.service.WaiterService;
import com.comfort.hotel.utils.StringTools;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
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


    //分页查询服务员信息
    @Override
    public PageInfo<WaiterDTO> findWaiterInList(PageDTO pageDTO) {
        // 第几页
        Integer page = pageDTO.getCurrentPage();
        // 每页显示的大小
        Integer size = pageDTO.getSize();
        // 使用pageHelper提高编码效率
        // 默认查询所有记录，PageHelper分页拦截器拦截，即在执行相关Sql之前会拦截自动加上limit
        PageHelper.startPage(page, size);
        List<WaiterDTO> list = waiterMapper.selectWaiterList(pageDTO.getKeyword(), pageDTO.getJobTypes());
        return new PageInfo<>(list);
    }

    // 根据工作类型查询不同的管理员人数
    @Override
    public Integer getWaiterCountByType(Integer[] types) {
      return waiterMapper.getWaiterCountByType(types);
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
    public void updateWaiterInfo(WaiterDTO waiterDTO) {
        Date date = new Date();

        //判断服务员是否修改的状态
        // 设置服务员上班时间
        if (Constants.ONE.equals(waiterDTO.getWaiterStatus())) {
            waiterDTO.setLastOfficeTime(date);
        }
        // 设置服务员休假时间
        if (Constants.TWO.equals(waiterDTO.getWaiterStatus())) {
          waiterDTO.setLastDayOffTime(date);
        }
        // 设置服务员离职时间
        if (Constants.THREE.equals(waiterDTO.getWaiterStatus())) {
            waiterDTO.setLastFireTime(date);
        }
        waiterMapper.updateWaiterInfo(waiterDTO);
    }

    // 删除当前服务员
    @Override
    public void deleteCurrentWaiter(String waiterId) {
        waiterMapper.deleteWaiterById(waiterId);
    }


    // 录入服务员信息
    @Override
    public void addWaiterInfo(WaiterDTO waiterDTO) {
        // 随机生成roomId
        waiterDTO.setWaiterId(StringTools.getRandomString(Constants.LENGTH_8));
        waiterMapper.addWaiterInfo(waiterDTO);
    }
}
