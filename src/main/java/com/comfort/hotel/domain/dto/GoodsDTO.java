package com.comfort.hotel.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: ayuan
 * @PS: 商品的dto
 * @Date: 2024/10/3 22:07
 */

@Data
public class GoodsDTO {
    private String goodsCover;
    private String goodsName;
    private BigDecimal positiveRating;
}