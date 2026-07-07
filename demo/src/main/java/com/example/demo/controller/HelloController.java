package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.HelloService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class HelloController {

//	단순연결
//	@Autowired
//	private HelloService helloService;
	
//	final키워드를 사용할 수 있다.(필수: 생성자 초기화방식)
	private final HelloService helloService;
	private final MessageComponent messageComponent;

	public HelloController(HelloService helloService, MessageComponent messageComponent) {
        this.helloService = helloService;
        this.messageComponent = messageComponent;
    }

	// http://localhost:8081/hello
	@GetMapping("/hello")
	public String hello() {
		return helloService.helloPrint();
	}
	
	// http://localhost:8081/message
	@GetMapping("/message")
	public String message() {
		return messageComponent.getMessage();
	}
	

}
