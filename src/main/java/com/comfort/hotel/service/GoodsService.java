package com.comfort.hotel.service;

import com.comfort.hotel.domain.vo.GoodsVO;
import com.comfort.hotel.domain.vo.PageVO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: ayuan
 * @PS: 商品信息接口
 * @Date: 2024/9/23 11:54
 */
public interface GoodsService {

    // 分页查询商品信息
    PageInfo<GoodsVO> findGoodsInList(PageVO pageVO);

    // 更新商品信息
    void updateGoodsInfo(GoodsVO goodsVO);

    // 更新商品图片
    void uploadGoodsCover(MultipartFile file);

    // 删除当前商品信息
    void deleteCurrentGoodsInfo(String goodsId);

    // 录入商品信息
    void addGoodsInfo(GoodsVO goodsVO);




}
