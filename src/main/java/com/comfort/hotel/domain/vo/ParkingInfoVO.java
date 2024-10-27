package com.comfort.hotel.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: ayuan
 * @PS: 停车信息VO实体
 * @Date: 2024/10/3 22:07
 */

@Data
public class ParkingInfoVO {

    private String carCover;
    private String carName;
    private String carNumber;
    private Integer carStatus;
    private Integer cumulativeCost;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime lastParkingTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime lastLeaveTime;
    private String parkingBoy;
}