package com.example.demo.calculator.service;

import org.springframework.stereotype.Service;

@Service
public class CalcService {

	public String getAddCalc(int a, int b) {
		// 연산 로직 수행
		return (a + b)+"";
	}

}
