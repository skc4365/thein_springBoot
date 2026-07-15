package com.example.jpa.member.service;

import com.example.jpa.member.domain.Member;
import com.example.jpa.member.domain.MemberRole;
import com.example.jpa.member.domain.MemberStatus;
import com.example.jpa.member.dto.MemberCreateRequest;
import com.example.jpa.member.dto.MemberResponse;
import com.example.jpa.member.dto.MemberUpdateRequest;
import com.example.jpa.member.exception.CustomException;
import com.example.jpa.member.exception.ErrorCode;
import com.example.jpa.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional // 테스트 완료 후 자동으로 롤백 처리
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 가입에 성공하고 ID가 발급되어야 한다.")
    void join_success() {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .email("test@example.com")
                .name("홍길동")
                .role(MemberRole.USER)
                .build();

        // when
        Long memberId = memberService.join(request);

        // then
        assertThat(memberId).isNotNull();
        Member findMember = memberRepository.findById(memberId).orElseThrow();
        assertThat(findMember.getEmail()).isEqualTo("test@example.com");
        assertThat(findMember.getName()).isEqualTo("홍길동");
        assertThat(findMember.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @Test
    @DisplayName("중복된 이메일로 가입을 시도하면 DUPLICATE_EMAIL 예외가 발생한다.")
    void join_duplicate_email() {
        // given
        MemberCreateRequest request1 = MemberCreateRequest.builder()
                .email("duplicate@example.com")
                .name("유저1")
                .build();
        memberService.join(request1);

        MemberCreateRequest request2 = MemberCreateRequest.builder()
                .email("duplicate@example.com")
                .name("유저2")
                .build();

        // when & then
        assertThatThrownBy(() -> memberService.join(request2))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.DUPLICATE_EMAIL.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 회원 조회 시 MEMBER_NOT_FOUND 예외가 발생한다.")
    void findMember_not_found() {
        // when & then
        assertThatThrownBy(() -> memberService.findMember(999L))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("회원 정보 수정 시 Dirty Checking(변경 감지)에 의해 수정사항이 반영되어야 한다.")
    void updateMember_dirty_checking() {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .email("update@example.com")
                .name("이전이름")
                .role(MemberRole.USER)
                .build();
        Long memberId = memberService.join(request);

        MemberUpdateRequest updateRequest = MemberUpdateRequest.builder()
                .name("새이름")
                .role(MemberRole.ADMIN)
                .build();

        // when
        memberService.updateMember(memberId, updateRequest);

        // 영속성 컨텍스트가 DB에 반영되도록 강제 플러시 후 클리어 (Dirty Checking 확인용)
        // 실제 운영 환경에서는 트랜잭션 종료 시점에 자동으로 반영됩니다.
        
        // then
        MemberResponse response = memberService.findMember(memberId);
        assertThat(response.getName()).isEqualTo("새이름");
        assertThat(response.getRole()).isEqualTo(MemberRole.ADMIN);
    }

    @Test
    @DisplayName("회원 탈퇴 시 상태가 DELETED로 변경(소프트 딜리트)되고 조회 시 미존재 처리된다.")
    void withdrawMember_soft_delete() {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .email("delete@example.com")
                .name("탈퇴자")
                .build();
        Long memberId = memberService.join(request);

        // when
        memberService.withdrawMember(memberId);

        // then
        // 1. Service 단건 조회를 시도하면 예외가 발생해야 함 (상태가 DELETED이므로)
        assertThatThrownBy(() -> memberService.findMember(memberId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());

        // 2. Repository 직접 조회 시에는 DB 상에 데이터가 남아있고 상태가 DELETED 임을 확인
        Member deletedMember = memberRepository.findById(memberId).orElseThrow();
        assertThat(deletedMember.getStatus()).isEqualTo(MemberStatus.DELETED);
    }
}
