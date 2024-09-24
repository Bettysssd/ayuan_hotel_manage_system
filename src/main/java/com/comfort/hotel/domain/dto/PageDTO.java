package com.comfort.hotel.domain.dto;


import lombok.Data;

/**
 * @Author: ayuan
 * @PS: 分页查询封装
 * @Date: 2024/9/5 15:10
 */

@Data
public class PageDTO {
    // 管理员模块使用，来区分不同的服务类型
    private int[] jobTypes;
    // 分页查询当前页数
    private Integer currentPage;
    // 分页查询当前页面显示条数
    private Integer size;
    // 分页查询模糊条件
    private String keyword;

}
