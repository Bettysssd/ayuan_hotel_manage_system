package com.comfort.hotel.domain.entity;

import lombok.Data;

/**
 * @Author: ayuan
 * @PS: 房间类实体
 * @Date: 2024/9/11 16:01
 */
@Data
public class Room {
    private String roomId;
    private String roomCover;
    private String roomInfo;
    private Integer roomType;
    private Integer roomStatus;
    private String roomMark;
    private Integer numberOfOccupants;
    private Integer price;
    private String bookTime;
    private String checkInTime;
    private String checkOutTime;
}
