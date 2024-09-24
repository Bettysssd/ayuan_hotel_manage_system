package com.comfort.hotel.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: ayuan
 * @PS: 服务员列表查询返回体
 * @Date: 2024/9/11 15:54
 */
@Data
public class WaiterDTO {

    private String waiterId;
    private String waiterCover;
    private String waiterName;
    private Integer gender;
    private Integer waiterStatus;
    private Integer jobType;
    private BigDecimal waiterPositiveRating;
    private Integer waiterTrumpUp;
    private Integer waiterTrumpDown;
    private String timeOfEmployment;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date lastOfficeTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date lastDayOffTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date lastFireTime;
}
