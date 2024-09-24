package com.comfort.hotel.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: ayuan
 * @PS: 阿里云存储对象服务配置
 * @Date: 2024/9/6 14:58
 */


@Configuration
@Data
public class OSSConfig {
    @Value("${aliyun.oss.end-point:}")
    private String endPoint;
    @Value("${aliyun.oss.access-key-id:}")
    private String accessKeyId;
    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;
    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;
}
