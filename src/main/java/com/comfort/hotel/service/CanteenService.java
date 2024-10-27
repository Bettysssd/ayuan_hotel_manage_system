package com.comfort.hotel.service;

import com.comfort.hotel.domain.vo.CanteenVO;
import com.comfort.hotel.domain.vo.PageVO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.List;

/**
 * @Author: ayuan
 * @PS: 食堂服务接口
 * @Date: 2024/9/18 11:46
 */
public interface CanteenService {

    // 分页查询食堂信息
    PageInfo<CanteenVO> findCanteenInList(PageVO pageVO);

    // 更新食堂信息
    void updateCanteenInfo(CanteenVO canteenVO);

    // 录入食堂信息
    void addCanteenInfo(CanteenVO canteenVO);

    // 更新食堂图片
    void uploadCanteenCover(MultipartFile file);

    // 删除当前食堂信息
    void deleteCurrentCanteenInfo(String canteenId);

    // 查找所有的食堂id
    List<String> selectAllCanteenIds();

    // 根据id查询食堂当前的状态
    Integer selectCanteenStatusById(String id);

    // 查询当前食堂的营业时间
    LocalTime selectOpenTimeById(String id);

    // 查询当前食堂的打烊时间
    LocalTime selectClosedTimeById(String id);

    // 更新食堂当前状态
    Integer updateCanteenOpenStatus(String id, Integer target);
}

