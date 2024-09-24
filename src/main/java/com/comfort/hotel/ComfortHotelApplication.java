package com.comfort.hotel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.comfort.hotel.mapper")
public class ComfortHotelApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComfortHotelApplication.class, args);
    }

}
