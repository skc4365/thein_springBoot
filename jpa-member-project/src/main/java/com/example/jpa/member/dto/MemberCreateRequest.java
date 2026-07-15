package com.example.jpa.member.dto;

import com.example.jpa.member.domain.Member;
import com.example.jpa.member.domain.MemberRole;
import com.example.jpa.member.domain.MemberStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberCreateRequest {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;

    private MemberRole role;

    public Member toEntity() {
        return Member.builder()
                .email(this.email)
                .name(this.name)
                .role(this.role != null ? this.role : MemberRole.USER)
                .status(MemberStatus.ACTIVE)
                .build();
    }
}
