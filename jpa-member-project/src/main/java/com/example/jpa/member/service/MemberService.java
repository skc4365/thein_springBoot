package com.example.jpa.member.service;

import com.example.jpa.member.domain.Member;
import com.example.jpa.member.domain.MemberStatus;
import com.example.jpa.member.dto.MemberCreateRequest;
import com.example.jpa.member.dto.MemberResponse;
import com.example.jpa.member.dto.MemberUpdateRequest;
import com.example.jpa.member.exception.CustomException;
import com.example.jpa.member.exception.ErrorCode;
import com.example.jpa.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     */
    @Transactional
    public Long join(MemberCreateRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        Member member = request.toEntity();
        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    /**
     * 단건 회원 조회 (Id 기반)
     */
    public MemberResponse findMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        
        // Soft delete 처리된 사용자는 미존재로 처리 가능
        if (member.getStatus() == MemberStatus.DELETED) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }
        return MemberResponse.from(member);
    }

    /**
     * 전체 회원 조회
     */
    public List<MemberResponse> findAllMembers() {
        return memberRepository.findAll().stream()
                .filter(m -> m.getStatus() == MemberStatus.ACTIVE)
                .map(MemberResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 회원 정보 수정 (Dirty Checking 적용)
     */
    @Transactional
    public void updateMember(Long id, MemberUpdateRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.getStatus() == MemberStatus.DELETED) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // 엔티티 필드 수정 -> 트랜잭션 커밋 시점에 변경 감지(Dirty Checking)가 작동하여 Update 쿼리 실행
        member.update(request.getName(), request.getRole());
    }

    /**
     * 회원 탈퇴 (Soft Delete)
     */
    @Transactional
    public void withdrawMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.getStatus() == MemberStatus.DELETED) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // 상태값만 DELETED로 변경하여 소프트 딜리트 처리 (Dirty Checking 활용)
        member.withdraw();
    }
}
