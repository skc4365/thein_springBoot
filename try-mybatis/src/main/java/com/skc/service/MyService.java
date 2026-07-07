package com.skc.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skc.dto.Member;
import com.skc.mapper.MyMapper;

@Service
public class MyService {
	
	private MyMapper myMapper;
	private MyService(MyMapper myMapper) {
		this.myMapper = myMapper;
	}
	

	public List<Member> findAll() {
		
		return myMapper.findAll();
	}

}
