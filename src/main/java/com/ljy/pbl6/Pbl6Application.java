package com.ljy.pbl6;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.ljy.pbl6.mapper")
public class Pbl6Application {

    public static void main(String[] args) {
        SpringApplication.run(Pbl6Application.class, args);
    }

}