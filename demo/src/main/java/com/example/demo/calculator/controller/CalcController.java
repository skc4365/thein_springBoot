package com.example.demo.calculator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class CalcController {
	
	@Autowired
	private CalcService calcService;
	
	@GetMapping("/add")
	public String add(@RequestParam int a, @RequestParam int b) {
		int result = 
		return "("
	}
	
	
	

}
