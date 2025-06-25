package com.wusd.codegenerator;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.wusd.codegenerator")
public class CGApplication {
    public static void main(String[] args) {
        SpringApplication.run(CGApplication.class, args);
    }
}
