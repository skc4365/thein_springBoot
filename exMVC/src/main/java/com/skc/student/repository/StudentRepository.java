package com.skc.student.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.skc.student.dto.Student;

@Repository
public class StudentRepository {
	
	private final List<Student> students = new ArrayList<>();
	
	public StudentRepository() {
		students.add(new Student(1, "홍길동", 20));
		students.add(new Student(2, "이순신", 22));
		students.add(new Student(3, "강감찬", 23));
	}

	// 값을 가져오는 녀석
	public List<Student> findAll() {
		
		return students;
	}

	
}
