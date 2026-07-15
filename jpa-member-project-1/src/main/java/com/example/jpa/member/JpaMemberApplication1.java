package com.example.jpa.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // BaseEntity 생성일/수정일 감지를 위해 필수 활성화
@SpringBootApplication
public class JpaMemberApplication1 {

    public static void main(String[] args) {
        SpringApplication.run(JpaMemberApplication1.class, args);
    }
}
