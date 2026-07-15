package com.example.jpa.member.dto;

import com.example.jpa.member.domain.MemberRole;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberUpdateRequest {

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;

    private MemberRole role;

    public MemberUpdateRequest(String name, MemberRole role) {
        this.name = name;
        this.role = role;
    }
}
