package com.koshailimited.demo2;

import com.koshailimited.demo2.model.DaoClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Demo2Application {

	@Autowired
	private DaoClass daoClass;

	public static void main(String[] args) {
		SpringApplication.run(Demo2Application.class, args);
	}

}
