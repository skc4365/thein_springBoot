package com.skc.board.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skc.board.dto.BoardDTO;
import com.skc.board.repository.BoardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

	// DB 사용하기 위한 준비
	private final BoardRepository repository;

	// 저장하기
	public void insert(BoardDTO dto) {
		repository.insert(dto);
	}

	// 전체목록
	public List<BoardDTO> findAll() {
		return repository.findAll();
	}
	
	// 상세검색
	public BoardDTO findByID(Long id) {
		return repository.findById(id);
	}
	
	// 수정
	public void update(BoardDTO dto) {
		repository.update(dto);
	}

	// 삭제
	public void delete(Long id) {
		repository.delete(id);
	}
}
