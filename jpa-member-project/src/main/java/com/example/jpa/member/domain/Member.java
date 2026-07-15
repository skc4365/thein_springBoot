package com.example.jpa.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberStatus status;

    @Builder
    public Member(String email, String name, MemberRole role, MemberStatus status) {
        this.email = email;
        this.name = name;
        this.role = role != null ? role : MemberRole.USER;
        this.status = status != null ? status : MemberStatus.ACTIVE;
    }

    // --- 비즈니스 메서드 (JPA Dirty Checking 활용) ---

    /**
     * 회원 이름 및 역할 정보 수정
     */
    public void update(String name, MemberRole role) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (role != null) {
            this.role = role;
        }
    }

    /**
     * 회원 탈퇴 (소프트 딜리트 처리)
     */
    public void withdraw() {
        this.status = MemberStatus.DELETED;
    }
}
