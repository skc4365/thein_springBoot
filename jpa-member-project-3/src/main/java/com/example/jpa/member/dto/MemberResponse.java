package com.example.jpa.member.dto;

import com.example.jpa.member.domain.Member;
import com.example.jpa.member.domain.MemberRole;
import com.example.jpa.member.domain.MemberStatus;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class MemberResponse {

    private final Long id;
    private final String email;
    private final String name;
    private final MemberRole role;
    private final MemberStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // 생성자 주입
    public MemberResponse(Long id, String email, String name, MemberRole role, MemberStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // 엔티티를 안전하게 Response DTO로 치환하는 정적 팩토리 메서드 제공
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getRole(),
                member.getStatus(),
                member.getCreatedAt(),
                member.getUpdatedAt()
        );
    }
}
