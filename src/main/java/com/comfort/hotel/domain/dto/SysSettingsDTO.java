package com.comfort.hotel.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: ayuan
 * @PS: 验证码发送模板
 * @Date: 2024/9/2 20:41
 */
@Data
public class SysSettingsDTO implements Serializable {

    // 邮箱发送头部
    private String registerEmailTitle = "【悦居酒店】";

    // 邮箱发送实体
    private String registerEmailContent = "悦居酒店欢迎您，验证码： %s, 请在5分钟内完成验证，如非本人操作，请忽略";


}
