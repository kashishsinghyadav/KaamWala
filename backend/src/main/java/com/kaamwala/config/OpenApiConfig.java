package com.kaamwala.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger UI configuration.
 *
 * <p>Configures the API documentation with JWT Bearer token security scheme
 * so that authenticated endpoints can be tested directly from Swagger UI.</p>
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configure the OpenAPI specification with project info and JWT security.
     *
     * @return the configured OpenAPI bean
     */
    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("KaamWala API")
                        .description("Hyperlocal Worker Marketplace - REST API Documentation")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("KaamWala Team")
                                .email("dev@kaamwala.com"))
                        .license(new License()
                                .name("Proprietary")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT token")));
    }
}
