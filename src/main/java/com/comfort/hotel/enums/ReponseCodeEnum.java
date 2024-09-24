package com.comfort.hotel.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: ayuan
 * @PS: 响应状态枚举类
 * @Date: 2024/7/29 18:32
 */
@Getter
@AllArgsConstructor
public enum ReponseCodeEnum {
    CODE_200(200, "请求成功"),
    CODE_404(404,"请求地址不存在"),
    CODE_500(500,"服务器返回错误，请联系管理员"),
    CODE_600(600,"请求参数错误"),
    CODE_601(601,"信息已经存在"),
    CODE_602(602, "文件下载地址为空或无效"),
    CODE_901(901,"登录超时，请重新登录"),
    CODE_902(902,"分享链接不存在，或者已失效"),
    CODE_903(903,"分享验证失败，请重新验证"),
    CODE_904(904,"网盘空间不足，请扩容");


    private Integer code;

    private String msg;

}
