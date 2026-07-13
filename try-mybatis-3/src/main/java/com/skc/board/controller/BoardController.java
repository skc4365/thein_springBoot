package com.skc.board.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.skc.board.dto.BoardDTO;
import com.skc.board.service.BoardService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class BoardController {

	private final BoardService service;

	// 저장페이지 save.html
	@GetMapping("/save")
	public String save() {

		// Thymleaf 템플릿 save.html 페이지를 찾음.
		return "save";
	}

	// 저장기능 수행후-> 전체목록 페이지로_redirect
	@PostMapping("/save")
	public String save(BoardDTO dto) {
		System.out.println("=== 콘솔값을 확인하자 ===");
		System.out.println("DTO : " + dto);

		service.insert(dto);

		return "redirect:/list";
	}

	// 전체목록 검색기능 수행후-> 전체목록_페이지로
	@GetMapping("/list")
	public String findAll(Model model) {
		List<BoardDTO> boardList = service.findAll();
		model.addAttribute("boardList", boardList);
		System.out.println("보드리스트 = " + boardList);
		return "list";
	}

	// 상세검색 기능수행후-> 상세페이지로
	@GetMapping("/{id}")
	public String findById(@PathVariable("id") Long id, Model model) {
		BoardDTO dto = service.findByID(id);
		model.addAttribute("board", dto);
		System.out.println("board dto: " + dto);
		return "detail";
	}

	// 상세검색 기능수행후-> 상세페이지로
	@GetMapping("/update/{id}")
	public String update(@PathVariable("id") Long id, Model model) {
		BoardDTO dto = service.findByID(id);
		model.addAttribute("board", dto);
		return "update";
	}

	// 수정 기능-> 상세검색-> 상세페이지로
	@PostMapping("/update/{id}")
	public String update(BoardDTO dto, Model model) {
		service.update(dto);
		BoardDTO dto2 = service.findByID(dto.getId());
		model.addAttribute("board", dto2);
		return "detail";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") Long id) {
		service.delete(id);
		return "redirect:/list";
	}

}
