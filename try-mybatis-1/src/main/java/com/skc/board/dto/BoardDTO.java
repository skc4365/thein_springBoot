package com.skc.board.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardDTO {
	private Long id;
	private String title;
	private String writer;
	private String pass;
	private String contents;

}
