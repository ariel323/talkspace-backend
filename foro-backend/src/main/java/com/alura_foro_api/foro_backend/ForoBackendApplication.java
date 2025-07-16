package com.alura_foro_api.foro_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ForoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForoBackendApplication.class, args);
	}

}
