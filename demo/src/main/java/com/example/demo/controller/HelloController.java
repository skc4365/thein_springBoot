package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.HelloService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class HelloController {

//	단순연결
//	@Autowired
//	private HelloService helloService;
	
//	final키워드를 사용할 수 있다.(필수: 생성자 초기화방식)
	private final HelloService helloService;

	public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

	
	@GetMapping("/hello")
	public String hello() {
		return helloService.helloPrint();
	}

}
