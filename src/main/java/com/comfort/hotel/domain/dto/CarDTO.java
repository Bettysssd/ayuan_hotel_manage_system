package com.comfort.hotel.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


/**
 * @Author: ayuan
 * @PS: 用户车辆查询返回体
 * @Date: 2024/9/5 15:14
 */
@Data
public class CarDTO {

    private String parkingId;
    private String carCover;
    private String carOwnerName;
    private String carOwnerContact;
    private Integer carStatus;
    private Integer cumulativeCost;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private String lastParkingTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private String lastLeaveTime;



}
