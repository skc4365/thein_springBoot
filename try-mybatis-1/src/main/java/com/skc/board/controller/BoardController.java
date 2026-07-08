package com.skc.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.skc.board.dto.BoardDTO;
import com.skc.board.service.BoardService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardController {

	private final BoardService service;
	
	@GetMapping("/save")
	public String save() {
		
		// Thymleaf 템플릿 save.html 페이지를 찾음.
		return "save";
	}
	
	@PostMapping("/save")
	public String save(BoardDTO dto) {
		System.out.println("=== 콘솔값을 확인하자 ===");
		System.out.println("DTO : " + dto);
		
		return "index";
	}
	
	

}
