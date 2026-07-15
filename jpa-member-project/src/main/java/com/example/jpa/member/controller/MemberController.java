package com.example.jpa.member.controller;

import com.example.jpa.member.dto.MemberCreateRequest;
import com.example.jpa.member.dto.MemberResponse;
import com.example.jpa.member.dto.MemberUpdateRequest;
import com.example.jpa.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원 가입 API
     */
    @PostMapping
    public ResponseEntity<Long> createMember(@RequestBody @Valid MemberCreateRequest request) {
        Long memberId = memberService.join(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(memberId);
    }

    /**
     * 회원 단건 조회 API
     */
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable Long id) {
        MemberResponse response = memberService.findMember(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 전체 회원 목록 조회 API
     */
    @GetMapping
    public ResponseEntity<List<MemberResponse>> getAllMembers() {
        List<MemberResponse> responses = memberService.findAllMembers();
        return ResponseEntity.ok(responses);
    }

    /**
     * 회원 정보 수정 API
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMember(
            @PathVariable Long id,
            @RequestBody @Valid MemberUpdateRequest request) {
        memberService.updateMember(id, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 회원 탈퇴 API
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> withdrawMember(@PathVariable Long id) {
        memberService.withdrawMember(id);
        return ResponseEntity.noContent().build();
    }
}
