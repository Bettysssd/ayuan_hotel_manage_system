package com.comfort.hotel.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.istack.internal.NotNull;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalTime;

/**
 * @Author: ayuan
 * @PS: 食堂信息交互体
 * @Date: 2024/9/18 11:39
 */
@Data
public class CanteenDTO {


    private String canteenId;
    private String canteenCover;
    private String canteenName;
    private Integer canteenFlavorType;
    private Integer canteenTableStatus;
    private Integer canteenOpenStatus;
    private Integer canteenNumberOfDiners;
    private Integer canteenTotalTables;
    private Integer canteenCurrentAvailableTables;
    private String canteenIntroduce;
    private String canteenOfferFood;
    @JsonFormat(pattern = "HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalTime canteenOpenTime;
    @JsonFormat(pattern = "HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalTime canteenClosedTime;
    private BigDecimal canteenRating;
    private Integer canteenTrumpUp;
    private Integer canteenTrumpDown;


}
