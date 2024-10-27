package com.comfort.hotel.domain.qo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: ayuan
 * @PS: 泊车Qo对象
 * @Date: 2024/10/3 22:07
 */

@Data
public class ParkingQO {

    @NotBlank(message = "车牌号不能为空")
    private String carNumber;
    @NotNull(message = "汽车名字")
    private String carName;
    private String carCover;
    @NotNull(message = "用户id不能为空")
    private String userId;
}