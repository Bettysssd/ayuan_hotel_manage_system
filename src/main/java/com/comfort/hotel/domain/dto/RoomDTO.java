package com.comfort.hotel.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * @Author: ayuan
 * @PS: 房间列表查询返回体
 * @Date: 2024/9/11 15:54
 */
@Data
public class RoomDTO {

    private String roomId;
    private String roomCover;
    private String roomInfo;
    private Integer roomType;
    private Integer roomStatus;
    private String roomMark;
    private Integer numberOfOccupants;
    private String listOfMembers;
    private BigDecimal roomRating;
    private Integer roomTrumpUp;
    private Integer roomTrumpDown;
    private Integer price;
    private String bookTime;
    private String checkInTime;
    private String checkOutTime;
}
