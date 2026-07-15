package com.example.jpa.member.dto;

import com.example.jpa.member.domain.Member;
import com.example.jpa.member.domain.MemberRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JSON 역직렬화 시 바인딩을 위한 기본 생성자
public class MemberCreateRequest {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;

    private MemberRole role;

    // 교육생들을 위해 롬복 대신 생성자를 통해 데이터를 주입받도록 구성
    public MemberCreateRequest(String email, String name, MemberRole role) {
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public Member toEntity() {
        return new Member(this.email, this.name, this.role);
    }
}
