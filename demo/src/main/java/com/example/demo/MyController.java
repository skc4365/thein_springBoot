package com.example.demo;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class MyController {

	@GetMapping("/myhello")
	public String myhello(@RequestParam String name) {
		return "myhello "+ name;
	}
	
}
