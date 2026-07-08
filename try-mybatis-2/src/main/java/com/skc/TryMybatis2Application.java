package com.skc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class TryMybatis2Application {

	public static void main(String[] args) {
		SpringApplication.run(TryMybatis2Application.class, args);
	}

}
