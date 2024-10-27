package com.comfort.hotel.mapper;

import com.comfort.hotel.domain.dto.GoodsDTO;
import com.comfort.hotel.domain.vo.GoodsVO;
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
    List<GoodsVO> selectGoodsList(String keyword);

    // 更新商品信息
    int updateGoodsInfo(@Param("bean") GoodsVO goodsVO);

    // 商品图片是否存在
    @Select("select count(1) from goods where goods_cover = #{goodsCover}")
    int isImgUrlPathExist(@Param("goodsCover") String goodsCover);

    // 删除当前商品信息
    @Delete("delete from goods where goods_id = #{goodsId}")
    void deleteCurrentGoodsInfoById(@Param("goodsId") String goodsId);

    // 录入商品信息
    void addGoodsInfo(@Param("bean") GoodsVO goodsVO);

    // 通过id寻找商品名字
    @Select("select goods_name from goods where goods_id = #{goodsId}")
    String selectGoodsNameById(@Param("goodsId") String goodsId);

    // 通过id寻找商品库存量
    @Select("select goods_amount from goods where goods_id = #{goodsId}")
    Integer selectGoodsTotalAmountById(@Param("goodsId") String goodsId);

    // 通过id寻找商品信息
    GoodsDTO selectGoodsById(@Param("goodsId") String goodsId);

    // 点赞数
    @Select("select goods_trump_up from goods where goods_id = #{goodsId}")
    Integer selectTrumpUp(@Param("goodsId") String goodsId);

    // 踩数
    @Select("select goods_trump_down from goods where goods_id = #{goodsId}")
    Integer selectTrumpDown(@Param("goodsId") String goodsId);
}
