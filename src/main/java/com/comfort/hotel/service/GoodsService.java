package com.comfort.hotel.service;

import com.comfort.hotel.domain.dto.GoodsDTO;
import com.comfort.hotel.domain.dto.PageDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: ayuan
 * @PS: 商品信息接口
 * @Date: 2024/9/23 11:54
 */
public interface GoodsService {

    // 分页查询商品信息
    PageInfo<GoodsDTO> findGoodsInList(PageDTO pageDTO);

    // 更新商品信息
    void updateGoodsInfo(GoodsDTO goodsDTO);

    // 更新商品图片
    void uploadGoodsCover(MultipartFile file);

    // 删除当前商品信息
    void deleteCurrentGoodsInfo(String goodsId);

    // 录入商品信息
    void addGoodsInfo(GoodsDTO goodsDTO);
}
