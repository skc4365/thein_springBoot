package com.example.demo.calculator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.calculator.service.CalcService;

import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class CalcController {
	

////  A.자바방식의 개발자가 직접 Obj를 관리는 방식
//	private CalcService calcService = new CalcService();
//	
//	// http://localhost:8081/add?a=3&b=5
//	@GetMapping("/add")
//	public String add(@RequestParam int a, @RequestParam int b) {
//		// 서비스단에서 구현해야할 계산 알고리즘.
//		int result = a + b;
//		return "비즈니스 로직을 직접 구현해서 값을 주는 방식: " + result ;
//	}
	
	
//	----------------
//	B. 스프링 방식: DI1: @Autowired 방식
	@Autowired
	private CalcService calcService;
	
//	스프링 방식: getMethod를 이용한 서비스단 사용방식
	public void getCalc(CalcService calcService) {
		this.calcService = calcService;
	}
	
	// http://localhost:8081/add-calc?a=10&b=20
	@GetMapping("add-calc")
	public String addCalc(@RequestParam int a, @RequestParam int b) {
		return calcService.getAddCalc(a,b);
	}
	
//	----------------
//	C. 스프링 방식	
	
	
	
	

}
