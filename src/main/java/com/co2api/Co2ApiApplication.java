package com.co2api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.springframework.boot.SpringApplication.run;

/**
 * Entry point for the CO2 Emission Calculation API.
 *
 * The @SpringBootApplication annotation combines:
 *   - @Configuration: marks this class as a source of bean definitions
 *   - @EnableAutoConfiguration: enables Spring Boot's auto-configuration mechanism
 *   - @ComponentScan: scans this package and sub-packages for Spring components
 */
@SpringBootApplication
public class Co2ApiApplication {

    public static void main(String[] args) {
        run(Co2ApiApplication.class, args);
    }
}
