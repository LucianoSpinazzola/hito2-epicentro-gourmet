package com.epicentrogourmet.hito2;

import org.modelmapper.ModelMapper; 
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.epicentrogourmet.hito2*"})
public class Hito2Application {

	public static void main(String[] args) {
		SpringApplication.run(Hito2Application.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
