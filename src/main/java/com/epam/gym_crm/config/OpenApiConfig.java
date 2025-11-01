package com.epam.gym_crm.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        SecurityScheme xUser = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("X-Username")
                .description("Username header");

        SecurityScheme xPass = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("X-Password")
                .description("Password header");

        SecurityRequirement global = new SecurityRequirement()
                .addList("X-Username")
                .addList("X-Password");

        return new OpenAPI()
                .info(new Info().title("Gym CRM API").version("v1"))
                .schemaRequirement("X-Username", xUser)
                .schemaRequirement("X-Password", xPass)
                .addSecurityItem(global);
    }


    @Bean
    GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .packagesToScan("com.epam.gym_crm.web")
                .build();
    }
}
