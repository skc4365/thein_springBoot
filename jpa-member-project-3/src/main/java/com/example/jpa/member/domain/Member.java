package com.example.jpa.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 스펙 준수를 위해 지연 로딩용 프록시 생성에 꼭 필요한 생성자
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

    // 명시적인 표준 생성자를 작성하여 불필요한 빌더 어노테이션 배제
    public Member(String email, String name, MemberRole role) {
        this.email = email;
        this.name = name;
        this.role = role != null ? role : MemberRole.USER;
        this.status = MemberStatus.ACTIVE;
    }

    // 도메인 비즈니스 수정 로직 (JPA Dirty Checking 활용)
    public void update(String name, MemberRole role) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (role != null) {
            this.role = role;
        }
    }

    public void withdraw() {
        this.status = MemberStatus.DELETED;
    }
}
