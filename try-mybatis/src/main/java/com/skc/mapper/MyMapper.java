package com.skc.mapper;

import java.util.List;
import com.skc.dto.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MyMapper {

	List<Member> findAll();

}
