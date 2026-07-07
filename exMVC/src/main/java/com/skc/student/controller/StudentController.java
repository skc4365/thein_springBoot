package com.skc.student.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skc.student.dto.Student;
import com.skc.student.service.StudentService;

@ResponseBody
@Controller
public class StudentController {
	
	
	private final StudentService service;
	
	public StudentController(StudentService service) {
		this.service = service;
	}
	
	// http://localhost:8082/students
	@GetMapping("/students")
	public List<Student> students() {
		
		// 제네릭:
		return service.findAll();
	}
	

}
