package com.epam.gym_crm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * Creates and configures the main Swagger Docket bean.
     * <p>
     * A Docket is the primary interface for configuring Swagger.
     * It tells Swagger which controllers to scan, which paths to include,
     * and what metadata to display.
     * <p>
     * Configuration details:
     * 1. DocumentationType.SWAGGER_2: Specifies we're using Swagger 2.0 specification
     * 2. .select(): Begins the API selection builder
     * 3. .apis(): Specifies which packages to scan for REST controllers
     * 4. .paths(): Specifies which URL paths to include (any() means all paths)
     * 5. .build(): Builds the Docket configuration
     * 6. .apiInfo(): Adds metadata about the API (title, description, version, etc.)
     *
     * @return Docket - The configured Swagger Docket bean
     * <p>
     * Access Points:
     * - Swagger UI: http://localhost:8080/swagger-ui.html
     * - API Docs JSON: http://localhost:8080/v2/api-docs
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                // Scan all controllers in the specified package
                // This will automatically find all @RestController classes
                .select()
                // Include all paths/endpoints found in the controllers
                // Could be restricted with PathSelectors.regex("/api/.*") to only include /api/* paths
                .apis(RequestHandlerSelectors.basePackage("com.epam.gym_crm.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    /**
     * Provides metadata and general information about the API.
     * <p>
     * This information appears at the top of the Swagger UI page and helps
     * developers understand what the API does, who maintains it, and how to contact support.
     * <p>
     * Metadata includes:
     * - Title: The name of the API (shown prominently in Swagger UI)
     * - Description: A brief overview of what the API does
     * - Version: Current version of the API (useful for version tracking)
     * - Contact: Contact information for API support/questions
     * <p>
     * Additional fields that could be added:
     * - .termsOfServiceUrl(): URL to terms of service
     * - .license(): License information (e.g., "Apache 2.0")
     * - .licenseUrl(): URL to the license text
     *
     * @return ApiInfo - Object containing all API metadata
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Gym CRM REST API")
                .description("REST API for Gym CRM System")
                .version("1.0.0")
                .contact(new Contact("Gym CRM Team", "", ""))
                .build();
    }
}