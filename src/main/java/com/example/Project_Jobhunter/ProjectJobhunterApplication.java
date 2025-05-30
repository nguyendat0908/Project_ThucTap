package com.example.Project_Jobhunter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ProjectJobhunterApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectJobhunterApplication.class, args);
	}

}
