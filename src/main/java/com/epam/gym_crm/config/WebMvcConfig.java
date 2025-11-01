package com.epam.gym_crm.config;

import com.epam.gym_crm.web.interceptor.AuthInterceptor;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
@ComponentScan("com.epam.gym_crm.web")
@Import({
        SpringDocConfiguration.class,
        SpringDocConfigProperties.class,
        SpringDocWebMvcConfiguration.class,
        SwaggerConfig.class,
        SwaggerUiConfigProperties.class,
        SwaggerUiOAuthProperties.class
})
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebMvcConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // public endpoints:
                        "/trainees",        // POST create trainee
                        "/trainers",        // POST create trainer
                        "/users/login",     // GET login
                        "/users/ping",      // for checking only
                        // swagger / openapi:
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/webjars/**"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .resourceChain(true)
                .addResolver(new org.springframework.web.servlet.resource.WebJarsResourceResolver());

        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations(
                        "classpath:/META-INF/resources/swagger-ui/",
                        "classpath:/META-INF/resources/webjars/swagger-ui/"
                );
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/swagger-ui")
                .setViewName("redirect:/swagger-ui/");

        registry.addViewController("/swagger-ui/")
                .setViewName("forward:/webjars/swagger-ui/index.html");

        registry.addViewController("/swagger-ui/index.html")
                .setViewName("forward:/webjars/swagger-ui/index.html");
    }
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}
