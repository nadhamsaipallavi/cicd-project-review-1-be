package com.propertymanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
    exclude = {SecurityAutoConfiguration.class},
    scanBasePackages = {"com.propertymanagement.config"}
)
@EntityScan("com.propertymanagement.model")
@EnableJpaRepositories("com.propertymanagement.repository")
public class MinimalApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinimalApplication.class, args);
    }
} 