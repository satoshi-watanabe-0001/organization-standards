package com.organization.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main Application Class for Spring Boot REST API.
 * 
 * <p>This application follows organization standards defined in:
 * <ul>
 *   <li>/01-coding-standards/java/ - Java coding standards</li>
 *   <li>/05-technology-stack/backend-stack.md - Technology stack</li>
 *   <li>/07-security-compliance/ - Security requirements</li>
 * </ul>
 * 
 * <p>Key Features:
 * <ul>
 *   <li>RESTful API with Spring Boot 3.2+</li>
 *   <li>JPA with PostgreSQL</li>
 *   <li>JWT Authentication</li>
 *   <li>Input Validation</li>
 *   <li>Global Exception Handling</li>
 *   <li>OpenAPI Documentation</li>
 *   <li>Caching Support</li>
 * </ul>
 * 
 * @author Organization Engineering Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
public class Application {

    /**
     * Main entry point for the Spring Boot application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
