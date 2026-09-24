package com.atlantic.atlanticapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.atlantic")
@EntityScan("com.atlantic.models")
@EnableJpaRepositories("com.atlantic.ISBServices")
public class AtlanticApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AtlanticApiApplication.class, args);
    }

}
