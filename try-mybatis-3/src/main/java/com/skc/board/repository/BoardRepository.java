package com.skc.board.repository;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.skc.board.dto.BoardDTO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BoardRepository {

	// 자동으로 Bean등록됨.
	private final SqlSessionTemplate sql;

	// 저장
	public void insert(BoardDTO dto) {
		// <mapper namespace="Board"> 참고
		// <insert id="insert" 참고
		sql.insert("Board.insert", dto);

	}

	// 목록검색
	public List<BoardDTO> findAll() {
		// <mapper namespace="Board"> 참고
		// <select id="findAll" 참고
		return sql.selectList("Board.findAll");
	}

	// 상세검색
	public BoardDTO findById(Long id) {
		// <mapper namespace="Board"> 참고
		// <select id="findById" 참고
		return sql.selectOne("Board.findById", id);
	}

	// 수정
	public void update(BoardDTO dto) {
		// <mapper namespace="Board"> 참고
		// <update id="update" 참고
		sql.update("Board.update", dto);
	}

	// 삭제
	public void delete(Long id) {
		// <mapper namespace="Board"> 참고
		// <delete id="delete" 참고
		sql.delete("Board.delete", id);
	}

}
