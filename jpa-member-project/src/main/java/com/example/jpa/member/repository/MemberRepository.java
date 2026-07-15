package com.example.jpa.member.repository;

import com.example.jpa.member.domain.Member;
import com.example.jpa.member.domain.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 이메일 중복 여부 확인
     */
    boolean existsByEmail(String email);

    /**
     * 특정 상태(ACTIVE 등)의 회원 중 이메일로 검색
     */
    Optional<Member> findByEmailAndStatus(String email, MemberStatus status);
}
