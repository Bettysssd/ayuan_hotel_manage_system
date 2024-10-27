package com.comfort.hotel.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.PutObjectRequest;
import com.comfort.hotel.config.OSSConfig;
import com.comfort.hotel.constants.Constants;
import com.comfort.hotel.domain.vo.GoodsVO;
import com.comfort.hotel.domain.vo.PageVO;
import com.comfort.hotel.mapper.GoodsMapper;
import com.comfort.hotel.service.GoodsService;
import com.comfort.hotel.service.SupervisorService;
import com.comfort.hotel.utils.StringTools;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: ayuan
 * @PS: 商品信息实现类
 * @Date: 2024/9/23 13:43
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private OSSConfig ossConfig;
    private Logger logger = LoggerFactory.getLogger(SupervisorService.class);

    // 分页查询商品信息
    @Override
    public PageInfo<GoodsVO> findGoodsInList(PageVO pageVO) {
        // 第几页
        Integer page = pageVO.getCurrentPage();
        // 每页显示的大小
        Integer size = pageVO.getSize();
        // 使用pageHelper提高编码效率
        // 默认查询所有记录，PageHelper分页拦截器拦截，即在执行相关Sql之前会拦截自动加上limit
        PageHelper.startPage(page, size);
        List<GoodsVO> list = goodsMapper.selectGoodsList(pageVO.getKeyword());
        return new PageInfo<>(list);
    }

    // 更新商品信息
    @Override
    public void updateGoodsInfo(GoodsVO goodsVO) {
        goodsMapper.updateGoodsInfo(goodsVO);
    }


    // 更新商品图片
    @Override
    public void uploadGoodsCover(MultipartFile file) {
        // 拼接完整的图片url路径
        String imgUrlPath = Constants.OSS_PATH + file.getOriginalFilename();
        // 判断数据库中是否已经存在，避免重复的上传到阿里云oss上，浪费资源
        int count = goodsMapper.isImgUrlPathExist(imgUrlPath);
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

    // 删除当前商品信息
    @Override
    public void deleteCurrentGoodsInfo(String goodsId) {
        goodsMapper.deleteCurrentGoodsInfoById(goodsId);
    }


    // 录入商品信息
    @Override
    public void addGoodsInfo(GoodsVO goodsVO) {
        // 随机生成roomId
        goodsVO.setGoodsId(StringTools.getRandomString(Constants.LENGTH_8));
        goodsMapper.addGoodsInfo(goodsVO);
    }
}
