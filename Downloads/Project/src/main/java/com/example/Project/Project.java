package com.example.Project;

import com.example.Project.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Project implements CommandLineRunner {

	@Autowired
	private ProductRepository productRepository;

	public static void main(String[] args) {
		SpringApplication.run(Project.class, args);
	}

	@Override
	public void run(String... args) {
		Product p = new Product();
		p.setId(1);
		p.setName("Test Product");
		p.setPrice(100);
		productRepository.save(p);
		System.out.println("Product saved!");
	}
}
