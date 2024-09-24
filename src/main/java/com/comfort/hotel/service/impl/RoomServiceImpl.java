package com.comfort.hotel.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.PutObjectRequest;
import com.comfort.hotel.config.OSSConfig;
import com.comfort.hotel.constants.Constants;
import com.comfort.hotel.domain.dto.PageDTO;
import com.comfort.hotel.domain.dto.RoomDTO;
import com.comfort.hotel.mapper.RoomMapper;
import com.comfort.hotel.service.RoomService;
import com.comfort.hotel.service.SupervisorService;
import com.comfort.hotel.utils.StringTools;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: ayuan
 * @PS: 客房管理接口实现类
 * @Date: 2024/9/2 16:12
 */
@Service
public class RoomServiceImpl implements RoomService {

    @Resource
    private RoomMapper roomMapper;
    @Resource
    private OSSConfig ossConfig;
    private Logger logger = LoggerFactory.getLogger(SupervisorService.class);


    //分页查询房间信息
    @Override
    public PageInfo<RoomDTO> findRoomInList(PageDTO pageDTO) {
        // 第几页
        Integer page = pageDTO.getCurrentPage();
        // 每页显示的大小
        Integer size = pageDTO.getSize();
        // 使用pageHelper提高编码效率
        // 默认查询所有记录，PageHelper分页拦截器拦截，即在执行相关Sql之前会拦截自动加上limit
        PageHelper.startPage(page, size);
        List<RoomDTO> list = roomMapper.selectRoomList(pageDTO.getKeyword());
        return new PageInfo<>(list);
    }

    // 根据条件获取当前房间的数量信息
    @Override
    public Integer distinguishVariableRoom(Integer[] types, Integer status) {
        return roomMapper.distinguishVariableRoom(types, status);
    }

    // 更新客房图片
    @Override
    public void uploadRoomCover(MultipartFile file) {
        // 拼接完整的图片url路径
        String imgUrlPath = Constants.OSS_PATH + file.getOriginalFilename();
        // 判断数据库中是否已经存在，避免重复的上传到阿里云oss上，浪费资源
        int count = roomMapper.isImgUrlPathExist(imgUrlPath);
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

    // 更新客房基本信息
    @Override
    public boolean updateRoomInfo(RoomDTO roomDTO) {
        // 判断是否是原来的房间号码
        String roomMark = roomMapper.getRoomMarkById(roomDTO.getRoomId());
        if (!roomDTO.getRoomMark().equals(roomMark)) {
            Integer count = roomMapper.checkRoomMarkIsExist(roomDTO.getRoomMark());
            if (!Constants.ZERO.equals(count)) {
                return false;
            }
        }
            roomMapper.updateRoomInfo(roomDTO);
            return true;
    }

    // 删除当前客房
    @Override
    public void deleteCurrentRoom(String roomId) {
        roomMapper.deleteRoomById(roomId);
    }

    // 修改当前房间状态
    @Override
    public void changeRoomStatus(RoomDTO roomDTO) {
        roomMapper.changeRoomStatus(roomDTO.getRoomStatus(), roomDTO.getRoomId());
    }

    // 录入房间信息
    @Override
    public boolean addRoomInfo(RoomDTO roomDTO) {
        // 随机生成roomId
        roomDTO.setRoomId(StringTools.getRandomString(Constants.LENGTH_7));
        // 判断待添加的房间号是否已经存在
        Integer count = roomMapper.checkRoomMarkIsExist(roomDTO.getRoomMark());
        if (!Constants.ZERO.equals(count)) {
            return true;
        } else {
            roomMapper.addRoomInfo(roomDTO);
            return false;
        }

    }


}
