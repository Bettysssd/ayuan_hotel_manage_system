package com.comfort.hotel.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
/**
 * @Author: ayuan
 * @PS: 订单业务对象
 * @Date: 2024/10/3 22:07
 */

@Data
public class OrderVO {

    private String userId;
    private String orderId;
    private String objectId;
    private String objectName;
    private Integer displayStatus;
    private Integer objectStatus;
    private Integer orderAmount;
    private Float orderSpend;
    private Integer orderStatus;
    private String waiterId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime bookTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime purchaseTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime expiredTime;
    private String orderInfo;

}