package com.example.jpa.member.controller;

import com.example.jpa.member.dto.MemberCreateRequest;
import com.example.jpa.member.dto.MemberResponse;
import com.example.jpa.member.dto.MemberUpdateRequest;
import com.example.jpa.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    // 롬복 어노테이션 대신 명시적 생성자 주입 설계
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * 회원 가입 API
     */
    @PostMapping
    public ResponseEntity<Long> createMember(@RequestBody @Valid MemberCreateRequest request) {
        Long memberId = memberService.join(request.getEmail(), request.getName(), request.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(memberId);
    }

    /**
     * 회원 단건 조회 API (Entity를 Response DTO로 치환하여 반환)
     */
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable Long id) {
        MemberResponse response = MemberResponse.from(memberService.findMember(id));
        return ResponseEntity.ok(response);
    }

    /**
     * 전체 회원 조회 API
     */
    @GetMapping
    public ResponseEntity<List<MemberResponse>> getAllMembers() {
        List<MemberResponse> responses = memberService.findAllMembers().stream()
                .map(MemberResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    /**
     * 회원 정보 수정 API
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMember(
            @PathVariable Long id,
            @RequestBody @Valid MemberUpdateRequest request) {
        memberService.updateMember(id, request.getName(), request.getRole());
        return ResponseEntity.ok().build();
    }

    /**
     * 회원 탈퇴 API (소프트 딜리트)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> withdrawMember(@PathVariable Long id) {
        memberService.withdrawMember(id);
        return ResponseEntity.noContent().build();
    }
}
