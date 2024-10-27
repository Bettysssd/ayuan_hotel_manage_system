package com.comfort.hotel.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * @Author: ayuan
 * @PS: 用户车辆查询返回体
 * @Date: 2024/9/5 15:14
 */
@Data
public class CarVO {

    private String parkingId;
    private String carCover;
    private String carName;
    private String carNumber;
    private String carOwnerName;
    private String carOwnerContact;
    private Integer carStatus;
    private Integer cumulativeCost;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime lastParkingTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime lastLeaveTime;



}
