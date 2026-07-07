package com.skc.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skc.dto.Member;
import com.skc.service.MyService;


@RestController
public class MyController {

	private MyService myService;
	
	private MyController(MyService myService) {
		this.myService = myService;
	}
	
	@GetMapping("/member")
	public List<Member> getMember() {
		return myService.findAll();
	}
	
	
}
