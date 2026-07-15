package com.example.jpa.member.service;

import com.example.jpa.member.domain.Member;
import com.example.jpa.member.domain.MemberRole;
import com.example.jpa.member.domain.MemberStatus;
import com.example.jpa.member.exception.CustomException;
import com.example.jpa.member.exception.ErrorCode;
import com.example.jpa.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 등록 시 중복되지 않는 이메일은 가입에 성공해야 한다.")
    void join_success() {
        // given & when
        Long memberId = memberService.join("test2@test.com", "테스터2", MemberRole.USER);

        // then
        assertThat(memberId).isNotNull();
        Member member = memberRepository.findById(memberId).orElseThrow();
        assertThat(member.getEmail()).isEqualTo("test2@test.com");
    }

    @Test
    @DisplayName("중복 이메일 가입 시 DUPLICATE_EMAIL 비즈니스 예외가 터져야 한다.")
    void join_duplicate_email() {
        // given
        memberService.join("dup@test.com", "가입자1", MemberRole.USER);

        // when & then
        assertThatThrownBy(() -> memberService.join("dup@test.com", "가입자2", MemberRole.USER))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.DUPLICATE_EMAIL.getMessage());
    }

    @Test
    @DisplayName("회원 정보 수정 시 트랜잭션 범위 내의 Dirty Checking 메커니즘을 통해 변경이 반영되어야 한다.")
    void update_dirty_checking() {
        // given
        Long memberId = memberService.join("update@test.com", "수정전", MemberRole.USER);

        // when
        memberService.updateMember(memberId, "수정후", MemberRole.ADMIN);

        // then
        Member member = memberRepository.findById(memberId).orElseThrow();
        assertThat(member.getName()).isEqualTo("수정후");
        assertThat(member.getRole()).isEqualTo(MemberRole.ADMIN);
    }

    @Test
    @DisplayName("회원 탈퇴(소프트 딜리트) 시 상태가 DELETED로 바뀌고, 단건 서비스 조회 시 MEMBER_NOT_FOUND가 발생한다.")
    void withdraw_soft_delete() {
        // given
        Long memberId = memberService.join("withdraw@test.com", "탈퇴예정", MemberRole.USER);

        // when
        memberService.withdrawMember(memberId);

        // then
        // 서비스 조회를 통해 존재하지 않음이 검증되어야 함
        assertThatThrownBy(() -> memberService.findMember(memberId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());

        // DB 테이블에는 데이터가 물리적으로 살아있고 상태값만 변경되었는지 레포지토리로 직접 검증
        Member dbMember = memberRepository.findById(memberId).orElseThrow();
        assertThat(dbMember.getStatus()).isEqualTo(MemberStatus.DELETED);
    }
}
