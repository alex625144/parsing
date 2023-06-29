package com.parsing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication()
@EnableScheduling
public class ParsingApplication {
    public static void main(String[] args) {
        SpringApplication.run(ParsingApplication.class, args);
    }
}
//exclude = {DataSourceAutoConfiguration.class