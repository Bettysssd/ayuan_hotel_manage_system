package com.comfort.hotel.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: ayuan
 * @PS: 服务员dto
 * @Date: 2024/10/3 22:07
 */

@Data
public class WaiterDTO {

    private String waiterCover;
    private String waiterName;
    private BigDecimal waiterPositiveRating;
    private Integer jobType;
}