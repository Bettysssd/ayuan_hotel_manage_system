package com.comfort.hotel.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: ayuan
 * @PS: 前端响应接受
 * @Date: 2024/9/3 18:44
 */
@Data
public class ResponseVO<T> implements Serializable {

    private Integer code; //编码：1成功，0和其它数字为失败
    private String msg; //错误信息
    private T data; //数据

    public static <T> ResponseVO<T> success() {
        ResponseVO<T> result = new ResponseVO<T>();
        result.code = 1;
        return result;
    }

    public static <T> ResponseVO<T> success(T object) {
        ResponseVO<T> result = new ResponseVO<T>();
        result.data = object;
        result.code = 1;
        return result;
    }

    public static <T> ResponseVO<T> error(String msg) {
        ResponseVO result = new ResponseVO();
        result.msg = msg;
        result.code = 0;
        return result;
    }
}
