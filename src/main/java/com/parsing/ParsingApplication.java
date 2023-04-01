package com.parsing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

@Service
@SpringBootApplication
@EnableScheduling
public class ParsingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParsingApplication.class, args);
    }
}
