package com.comfort.hotel.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: ayuan
 * @PS: 服务员评价信息VO实体
 * @Date: 2024/10/3 22:07
 */
@Data
public class ReviewVO {

    private String goodsCover;
    private String goodsName;
    private BigDecimal goodsPositiveRating;
    private String waiterCover;
    private String waiterName;
    private String waiterJobType;
    private Integer jobType;
    private BigDecimal waiterPositiveRating;
}