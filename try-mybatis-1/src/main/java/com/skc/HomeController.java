package com.skc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	@GetMapping("/")
	public String index() {
		
		// Thymleaf 템플릿 index.html 페이지를 찾음.
		return "index";
	}

}
