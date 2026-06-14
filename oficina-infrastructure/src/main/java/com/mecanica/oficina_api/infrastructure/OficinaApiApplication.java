package com.mecanica.oficina_api.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.mecanica.oficina_api")
@EnableJpaRepositories(basePackages = "com.mecanica.oficina_api.adapters.persistence.repository")
@EntityScan(basePackages = "com.mecanica.oficina_api.adapters.persistence")
public class OficinaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OficinaApiApplication.class, args);
	}

}