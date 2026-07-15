package com.example.jpa.member.repository;

import com.example.jpa.member.domain.Member;
import com.example.jpa.member.domain.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    Optional<Member> findByEmailAndStatus(String email, MemberStatus status);
}
