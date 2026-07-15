package com.example.jpa.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class JpaMemberApplication3 {

    public static void main(String[] args) {
        SpringApplication.run(JpaMemberApplication3.class, args);
    }
}
