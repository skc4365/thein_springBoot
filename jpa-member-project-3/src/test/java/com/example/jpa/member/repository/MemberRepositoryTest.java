package com.example.jpa.member.repository;

import com.example.jpa.member.domain.Member;
import com.example.jpa.member.domain.MemberRole;
import com.example.jpa.member.domain.MemberStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // JPA 레포지토리 관련 빈만 로드하며, 테스트가 완료되면 자동으로 롤백을 수행합니다.
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원을 저장하고 저장된 회원 정보를 정상적으로 조회할 수 있어야 한다.")
    void saveAndFindMember() {
        // given
        Member member = new Member("user@test.com", "테스터", MemberRole.USER);

        // when
        Member savedMember = memberRepository.save(member);

        // then
        assertThat(savedMember.getId()).isNotNull();
        assertThat(savedMember.getEmail()).isEqualTo("user@test.com");
        assertThat(savedMember.getName()).isEqualTo("테스터");
        assertThat(savedMember.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @Test
    @DisplayName("이메일 존재 여부를 정상적으로 판단할 수 있어야 한다.")
    void existsByEmail() {
        // given
        Member member = new Member("exists@test.com", "기존가입자", MemberRole.USER);
        memberRepository.save(member);

        // when
        boolean isExist = memberRepository.existsByEmail("exists@test.com");
        boolean isNotExist = memberRepository.existsByEmail("notfound@test.com");

        // then
        assertThat(isExist).isTrue();
        assertThat(isNotExist).isFalse();
    }

    @Test
    @DisplayName("이메일과 회원 상태(ACTIVE)를 조합하여 회원을 조회할 수 있어야 한다.")
    void findByEmailAndStatus() {
        // given
        Member member = new Member("active@test.com", "활동회원", MemberRole.USER);
        memberRepository.save(member);

        // when
        Optional<Member> foundActive = memberRepository.findByEmailAndStatus("active@test.com", MemberStatus.ACTIVE);
        Optional<Member> foundDeleted = memberRepository.findByEmailAndStatus("active@test.com", MemberStatus.DELETED);

        // then
        assertThat(foundActive).isPresent();
        assertThat(foundActive.get().getName()).isEqualTo("활동회원");
        assertThat(foundDeleted).isEmpty();
    }
}
