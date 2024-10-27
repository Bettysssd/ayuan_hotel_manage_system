package com.comfort.hotel.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author: ayuan
 * @PS: 商品信息DTO
 * @Date: 2024/9/23 12:08
 */
@Data
public class GoodsVO {

    private String goodsId;
    private String goodsCover;
    private String goodsName;
    private BigDecimal price;
    private Integer goodsAmount;
    private Integer goodsType;
    private Integer goodsStatus;
    private String goodsInfo;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime productTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime expireTime;
    private BigDecimal positiveRating;
    private Integer goodsTrumpUp;
    private Integer goodsTrumpDown;

}
