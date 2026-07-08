package com.skc.board.repository;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.skc.board.dto.BoardDTO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BoardRepository {
	
	// 자동으로 Bran등록됨.
	private final SqlSessionTemplate sql;
	
	public void insert(BoardDTO dto) {
		// <mapper namespace="Board"> 참고
		// <insert id="insert" 참고
		sql.insert("Board.insert", dto);
		
	}

	public List<BoardDTO> findAll() {
		// <mapper namespace="Board"> 참고
		// <select id="findAll" 참고
		return sql.selectList("Board.findAll");
	}

}

