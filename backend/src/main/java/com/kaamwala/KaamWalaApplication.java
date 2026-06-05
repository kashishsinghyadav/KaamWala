package com.kaamwala;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


/**
 * Main entry point for the KaamWala hyperlocal worker marketplace backend.
 *
 * <p>This application provides REST APIs for connecting customers with
 * skilled workers (carpenters, electricians, plumbers, etc.) in their locality.</p>
 */


@SpringBootApplication
@EnableJpaAuditing
public class KaamWalaApplication {

    public static void main(String[] args) {
        SpringApplication.run(KaamWalaApplication.class, args);

    }

}
