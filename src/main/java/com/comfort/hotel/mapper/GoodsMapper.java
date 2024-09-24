package com.comfort.hotel.mapper;

import com.comfort.hotel.domain.dto.GoodsDTO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: ayuan
 * @PS: 商品信息mapper
 * @Date: 2024/9/23 13:45
 */
public interface GoodsMapper {
    // 分页查询商品信息
    List<GoodsDTO> selectGoodsList(String keyword);

    // 更新商品信息
    void updateGoodsInfo(@Param("bean") GoodsDTO goodsDTO);

    // 商品图片是否存在
    @Select("select count(1) from goods where goods_cover = #{goodsCover}")
    int isImgUrlPathExist(@Param("goodsCover") String goodsCover);

    // 删除当前商品信息
    @Delete("delete from goods where goods_id = #{goodsId}")
    void deleteCurrentGoodsInfoById(@Param("goodsId") String goodsId);

    // 录入商品信息
    void addGoodsInfo(@Param("bean") GoodsDTO goodsDTO);
}
