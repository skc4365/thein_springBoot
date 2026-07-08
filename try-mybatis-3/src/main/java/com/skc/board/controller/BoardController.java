package com.skc.board.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
		
		service.insert(dto);
		
		return "redirect:/list";
	}
	
	@GetMapping("/list")
	public String findAll(Model model) {
		List<BoardDTO> boardList = service.findAll();
		model.addAttribute("boardList", boardList);
		System.out.println("보드리스트 = " + boardList);
		return "list";
	}

}
