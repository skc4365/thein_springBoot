package com.example.jpa.member.service;

import com.example.jpa.member.domain.Member;
import com.example.jpa.member.domain.MemberRole;
import com.example.jpa.member.domain.MemberStatus;
import com.example.jpa.member.exception.CustomException;
import com.example.jpa.member.exception.ErrorCode;
import com.example.jpa.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true) // 성능 향상 및 Dirty Checking을 비활성화하여 조회의 안전성 보장
public class MemberService {

    private final MemberRepository memberRepository;

    // 롬복 @RequiredArgsConstructor 대신 명시적인 생성자 주입을 작성하여 결합 원리를 학습
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * 회원 가입
     */
    @Transactional // 쓰기 트랜잭션 필요
    public Long join(String email, String name, MemberRole role) {
        if (memberRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        Member member = new Member(email, name, role);
        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    /**
     * 회원 단건 조회
     */
    public Member findMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.getStatus() == MemberStatus.DELETED) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }
        return member;
    }

    /**
     * 회원 전체 조회
     */
    public List<Member> findAllMembers() {
        return memberRepository.findAll().stream()
                .filter(member -> member.getStatus() == MemberStatus.ACTIVE)
                .toList();
    }

    /**
     * 회원 수정 (Dirty Checking)
     */
    @Transactional
    public void updateMember(Long id, String name, MemberRole role) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.getStatus() == MemberStatus.DELETED) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // 엔티티 정보 변경 -> 트랜잭션 커밋될 때 더티체킹으로 자동 반영
        member.update(name, role);
    }

    /**
     * 회원 탈퇴 (소프트 딜리트)
     */
    @Transactional
    public void withdrawMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.getStatus() == MemberStatus.DELETED) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // 엔티티 상태 변경 -> 트랜잭션 종료 시 더티체킹 반영
        member.withdraw();
    }
}
