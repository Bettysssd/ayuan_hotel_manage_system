package com.comfort.hotel.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: ayuan
 * @PS:
 * @Date: 2024/9/2 20:38
 */
@Getter
@Configuration
public class EmailConfig {

    @Value("${spring.mail.username:}")
    private String sendUserName;

}
