package com.comfort.hotel.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: ayuan
 * @PS: 客房的dto
 * @Date: 2024/10/3 22:07
 */

@Data
public class CanteenDTO {

    private String canteenCover;
    private String canteenName;
    private BigDecimal canteenRating;
}