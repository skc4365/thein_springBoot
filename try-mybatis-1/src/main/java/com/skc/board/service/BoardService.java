package com.skc.board.service;

import org.springframework.stereotype.Service;

import com.skc.board.repository.BoardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {
	
	// DB 사용하기 위한 준비
	private final BoardRepository repository;

}
