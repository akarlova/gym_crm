package com.epam.gym_crm.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;

@Configuration
public class OpenApiConfig {
    private static final String BEARER_KEY = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme bearer = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Paste JWT here (without 'Bearer ')");

        return new OpenAPI()
                .info(new Info().title("Gym CRM API").version("v1"))
                .components(new Components().addSecuritySchemes(BEARER_KEY, bearer))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_KEY));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .packagesToScan("com.epam.gym_crm")
                .pathsToMatch("/**")
                .build();
    }
}
