package com.comfort.hotel.service.impl;


import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.PutObjectRequest;
import com.comfort.hotel.config.OSSConfig;
import com.comfort.hotel.constants.Constants;
import com.comfort.hotel.domain.vo.CanteenVO;
import com.comfort.hotel.domain.vo.PageVO;
import com.comfort.hotel.mapper.CanteenMapper;
import com.comfort.hotel.service.CanteenService;
import com.comfort.hotel.service.SupervisorService;
import com.comfort.hotel.utils.StringTools;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.LocalTime;
import java.util.List;

/**
 * @Author: ayuan
 * @PS: 食堂服务接口服务实现类
 * @Date: 2024/9/18 11:47
 */
@Service
public class CanteenServiceImpl implements CanteenService {

    @Resource
    private CanteenMapper canteenMapper;
    @Resource
    private OSSConfig ossConfig;
    private Logger logger = LoggerFactory.getLogger(SupervisorService.class);



    // 分页查询食堂信息
    @Override
    public PageInfo<CanteenVO> findCanteenInList(PageVO pageVO) {
        List<CanteenVO> list = null;
        if (pageVO != null) {
            // 第几页
            Integer page = pageVO.getCurrentPage();
            // 每页显示的大小
            Integer size = pageVO.getSize();
            // 使用pageHelper提高编码效率
            // 默认查询所有记录，PageHelper分页拦截器拦截，即在执行相关Sql之前会拦截自动加上limit
            PageHelper.startPage(page, size);
            list = canteenMapper.selectCanteenList(pageVO.getKeyword());
        } else {
            list = canteenMapper.selectCanteenList("");
        }
        return new PageInfo<>(list);
    }

    // 更新食堂信息
    @Override
    public void updateCanteenInfo(CanteenVO canteenVO) {
        canteenMapper.updateCanteenInfo(canteenVO);
    }

    // 录入食堂信息
    @Override
    public void addCanteenInfo(CanteenVO canteenVO) {
        // 随机生成roomId
        canteenVO.setCanteenId(StringTools.getRandomString(Constants.LENGTH_8));
        canteenMapper.addCanteenInfo(canteenVO);
    }

    // 更新食堂信息
    @Override
    public void uploadCanteenCover(MultipartFile file) {
        // 拼接完整的图片url路径
        String imgUrlPath = Constants.OSS_PATH + file.getOriginalFilename();
        // 判断数据库中是否已经存在，避免重复的上传到阿里云oss上，浪费资源
        int count = canteenMapper.isImgUrlPathExist(imgUrlPath);
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

    // 删除当前食堂信息
    @Override
    public void deleteCurrentCanteenInfo(String canteenId) {
         canteenMapper.deleteCurrentCanteenInfoById(canteenId);
    }

    // 查找所有的食堂id
    @Override
    public List<String> selectAllCanteenIds() {
        return canteenMapper.selectAllCanteenIds();
    }

    // 根据id查询食堂当前的状态
    @Override
    public Integer selectCanteenStatusById(String id) {
        return canteenMapper.selectCanteenStatusById(id);
    }

    // 查询当前食堂的营业时间
    @Override
    public LocalTime selectOpenTimeById(String id) {
        return canteenMapper.selectOpenTimeById(id);
    }

    // 查询当前食堂的打烊时间
    @Override
    public LocalTime selectClosedTimeById(String id) {
        return canteenMapper.selectClosedTimeById(id);
    }

    // 更新食堂当前状态
    @Override
    public Integer updateCanteenOpenStatus(String id, Integer target) {
        return canteenMapper.updateCanteenOpenStatus(id, target);
    }

}
