package com.comfort.hotel.controller;
import com.comfort.hotel.domain.vo.GoodsVO;
import com.comfort.hotel.service.GoodsService;
import com.comfort.hotel.domain.vo.PageVO;
import com.comfort.hotel.domain.vo.ResponseVO;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @Author: ayuan
 * @PS: 商品信息控制类
 * @Date: 2024/9/23 11:48
 */
@RestController
@RequestMapping("/supervisor")
@Api(tags = "商品信息控制类")
public class GoodsController {

    @Resource
    private GoodsService GoodsService;


    /**
     * 分页查询商品信息
     *
     * @param pageVO
     * @return
     */
    @ApiOperation(value = "分页查询商品信息")
    @PostMapping("/queryGoodsList")
    public ResponseVO<PageInfo<GoodsVO>> queryGoodsList(@RequestBody PageVO pageVO) {
        PageInfo<GoodsVO> goodsInInList = GoodsService.findGoodsInList(pageVO);
        return ResponseVO.success(goodsInInList);
    }


    /**
     * 更新商品信息
     *
     * @param goodsVO
     */
    @ApiOperation(value = "更新商品信息")
    @PostMapping("/updateGoodsInfo")
    public void updateGoodsInfo(@RequestBody GoodsVO goodsVO) {
        GoodsService.updateGoodsInfo(goodsVO);
    }

    /**
     * 更新商品图片
     */
    @ApiOperation(value = "更新商品图片")
    @PostMapping("/uploadGoodsCover")
    public void uploadGoodsCover(@RequestParam("file") MultipartFile file) {
        GoodsService.uploadGoodsCover(file);
    }

    /**
     * 删除当前商品信息
     *
     * @param goodsId
     */
    @ApiOperation(value = "删除当前商品信息")
    @DeleteMapping("/deleteCurrentGoodsInfo")
    public void deleteCurrentGoodsInfo(@RequestParam("goodsId") String goodsId) {
        GoodsService.deleteCurrentGoodsInfo(goodsId);
    }


    /**
     * 录入商品信息
     * @param goodsVO
     */
    @ApiOperation(value = "录入商品信息")
    @PostMapping("/addGoodsInfo")
    public void addGoodsInfo(@RequestBody GoodsVO goodsVO) {
        GoodsService.addGoodsInfo(goodsVO);
    }
}
