package com.comfort.hotel.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: ayuan
 * @PS: 客房的dto
 * @Date: 2024/10/3 22:07
 */

@Data
public class RoomDTO {

    private String roomCover;
    private Integer roomType;
    private BigDecimal roomRating;
}