package com.epam.gym_crm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.epam.gym_crm.controller")
public class WebConfig implements WebMvcConfigurer {

    /**
     * Creates and configures the Jackson ObjectMapper bean.
     * <p>
     * ObjectMapper is the main class in Jackson library responsible for:
     * - Serializing Java objects to JSON (object → JSON string)
     * - Deserializing JSON to Java objects (JSON string → object)
     * <p>
     * Configuration Details:
     * <p>
     * 1. JavaTimeModule Registration:
     * - Adds support for Java 8 date/time types (LocalDate, LocalDateTime, etc.)
     * - Without this, Jackson wouldn't know how to serialize LocalDate
     * - Example: LocalDate(2025-10-25) → "2025-10-25"
     * <p>
     * 2. WRITE_DATES_AS_TIMESTAMPS Disabled:
     * - By default, Jackson writes dates as timestamps (milliseconds since epoch)
     * - Disabling this makes dates readable: "2025-10-25" instead of 1729814400000
     * - More human-readable in API responses
     * - Follows ISO-8601 standard format
     * <p>
     * Why a Bean?
     * - Singleton object shared across the entire application
     * - Can be injected anywhere JSON processing is needed
     * - Ensures consistent JSON serialization everywhere
     * <p>
     * Example JSON Output:
     * WITH timestamps (default):
     * {
     * "dateOfBirth": 1729814400000
     * }
     * <p>
     * WITHOUT timestamps (our config):
     * {
     * "dateOfBirth": "1990-05-15"
     * }
     * <p>
     * Other Common ObjectMapper Configurations:
     * - setSerializationInclusion(Include.NON_NULL): Don't include null fields
     * - configure(FAIL_ON_UNKNOWN_PROPERTIES, false): Ignore unknown JSON fields
     * - setPropertyNamingStrategy(SNAKE_CASE): Use snake_case instead of camelCase
     *
     * @return ObjectMapper - Configured Jackson ObjectMapper instance
     * @see com.fasterxml.jackson.databind.ObjectMapper
     * @see com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    /**
     * Configures HTTP message converters for request/response body transformation.
     * <p>
     * Message Converters are responsible for:
     * - Converting HTTP request bodies to Java objects (@RequestBody)
     * - Converting Java objects to HTTP response bodies (@ResponseBody)
     * - Content negotiation (JSON, XML, etc.)
     * <p>
     * Flow of a Request:
     * <p>
     * 1. Client sends JSON:
     * POST /api/trainees/register
     * Content-Type: application/json
     * {"firstName": "John", "lastName": "Doe"}
     * <p>
     * 2. Message Converter (JSON → Java):
     * - Spring sees Content-Type: application/json
     * - Selects MappingJackson2HttpMessageConverter
     * - Converter uses ObjectMapper to deserialize JSON
     * - Creates TraineeRegistrationRequest object
     * <p>
     * 3. Controller Method:
     * public ResponseEntity<RegistrationResponse> register(
     *
     * @param converters - List to which converters should be added
     *                   Spring will use converters in the order they're added
     * @RequestBody TraineeRegistrationRequest request) {
     * // request is now a Java object!
     * }
     * <p>
     * 4. Response (Java → JSON):
     * - Controller returns RegistrationResponse object
     * - Spring sees Accept: application/json header
     * - Selects same converter
     * - Converter serializes object to JSON
     * - Response: {"username": "John.Doe", "password": "xyz123"}
     * <p>
     * Why Custom Configuration?
     * - We need our custom ObjectMapper with JavaTimeModule
     * - Default converter might not handle LocalDate correctly
     * - Ensures consistent date formatting across all endpoints
     * <p>
     * Multiple Converters:
     * Spring MVC can have multiple converters for different content types:
     * - MappingJackson2HttpMessageConverter: application/json
     * - Jaxb2RootElementHttpMessageConverter: application/xml
     * - StringHttpMessageConverter: text/plain
     * - ByteArrayHttpMessageConverter: application/octet-stream
     * <p>
     * Content Negotiation:
     * Spring selects the appropriate converter based on:
     * - Request Content-Type header (for @RequestBody)
     * - Request Accept header (for @ResponseBody)
     * - Controller @Produces/@Consumes annotations
     * @see org.springframework.http.converter.HttpMessageConverter
     * @see org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
     * @see org.springframework.web.bind.annotation.RequestBody
     * @see org.springframework.web.bind.annotation.ResponseBody
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // creates JSON message converter with our custom ObjectMapper
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        // uses our configured ObjectMapper (with JavaTimeModule and date settings)
        converter.setObjectMapper(objectMapper());
        // add converter to the list (Spring will use it for JSON conversion)
        converters.add(converter);
    }

    /**
     * Registers resource handlers for serving static resources.
     * <p>
     * Static Resources are files that don't require server-side processing:
     * - HTML files
     * - CSS stylesheets
     * - JavaScript files
     * - Images
     * - Swagger UI files
     * - WebJars (JavaScript/CSS libraries packaged as JARs)
     * <p>
     * Why Needed?
     * - DispatcherServlet is mapped to "/" (handles ALL requests)
     * - Without configuration, Spring would try to find controllers for static files
     * - Resource handlers tell Spring: "These paths are static, serve them directly"
     * <p>
     * Resource Handler 1: Swagger UI HTML
     * =====================================
     * Pattern: "swagger-ui.html"
     * Location: "classpath:/META-INF/resources/"
     * <p>
     * - Serves the main Swagger UI page
     * - When user visits: http://localhost:8080/swagger-ui.html
     * - Spring looks for: /META-INF/resources/swagger-ui.html in classpath
     * - File comes from springfox-swagger-ui Maven dependency
     * - Contains the interactive API documentation interface
     * <p>
     * Classpath Location Explanation:
     * - "classpath:" means look inside JAR files and src/main/resources
     * - Maven dependencies are on the classpath
     * - springfox-swagger-ui JAR contains /META-INF/resources/swagger-ui.html
     * - Spring automatically finds and serves it
     * <p>
     * Resource Handler 2: WebJars
     * ============================
     * Pattern: "/webjars/**"
     * Location: "classpath:/META-INF/resources/webjars/"
     * <p>
     * - Serves JavaScript/CSS libraries packaged as Maven dependencies
     * - "**" means match any sub-path (e.g., /webjars/jquery/3.5.1/jquery.js)
     * - Swagger UI needs these for styling and functionality
     * <p>
     * Example Request Flow:
     * 1. User opens: http://localhost:8080/swagger-ui.html
     * 2. HTML page loads and references: /webjars/springfox-swagger-ui/swagger-ui.css
     * 3. Browser requests: http://localhost:8080/webjars/springfox-swagger-ui/swagger-ui.css
     * 4. Spring matches "/webjars/**" pattern
     * 5. Looks in: classpath:/META-INF/resources/webjars/springfox-swagger-ui/swagger-ui.css
     * 6. Finds file in JAR and serves it
     * <p>
     * What are WebJars?
     * - Maven/Gradle dependencies for client-side libraries
     * - Package JavaScript/CSS as JAR files
     * - Provides version management for frontend dependencies
     * - Example: Instead of manually downloading jQuery, add webjar dependency
     * <p>
     * Benefits:
     * - No need to manually download and store static files
     * - Version controlled through Maven
     * - Works with build tools and CI/CD
     * - Consistent with backend dependency management
     * <p>
     * Without This Configuration:
     * - Swagger UI wouldn't load (404 errors)
     * - DispatcherServlet would look for controllers
     * - No controller for /swagger-ui.html → 404 Not Found
     * <p>
     * Additional Resource Handlers (examples not currently used):
     * <p>
     * Serve custom static files:
     * registry.addResourceHandler("/static/**")
     * .addResourceLocations("classpath:/static/");
     * <p>
     * Serve images:
     * registry.addResourceHandler("/images/**")
     * .addResourceLocations("classpath:/images/")
     * .setCachePeriod(3600); // Cache for 1 hour
     *
     * @param registry - ResourceHandlerRegistry for registering resource handlers
     *                 Provides fluent API for configuring static resource serving
     * @see org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
     * @see org.springframework.web.servlet.resource.ResourceHttpRequestHandler
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Handler for Swagger UI main page
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        // Handler for WebJars (Swagger UI dependencies like CSS, JS)
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * Enables default servlet handling for requests not handled by Spring MVC.
     * <p>
     * Purpose:
     * This configuration handles the "catch-all" scenario when Spring MVC
     * cannot find a matching controller for a request.
     * <p>
     * Problem It Solves:
     * - DispatcherServlet is mapped to "/" (all requests)
     * - What happens when no controller matches a request?
     * - Without this: 404 error "No mapping found"
     * - With this: Forwards to container's default servlet
     * <p>
     * What is the Default Servlet?
     * Every servlet container (Tomcat, Jetty, etc.) has a default servlet that:
     * - Serves static files from the webapp directory
     * - Handles requests that no other servlet claims
     * - Usually named "default" in Tomcat
     * <p>
     * How It Works:
     * <p>
     * 1. Request arrives: GET /some/unknown/path
     * 2. DispatcherServlet checks all controllers → no match found
     * 3. DispatcherServlet checks resource handlers → no match
     * 4. Instead of 404, forwards to default servlet
     * 5. Default servlet tries to find static file
     * 6. If static file exists → serves it
     * 7. If static file doesn't exist → 404 from default servlet
     * <p>
     * Request Processing Order:
     * <p>
     * HTTP Request
     * │
     * ▼
     * ┌─────────────────────┐
     * │ DispatcherServlet   │
     * └──────────┬──────────┘
     * │
     * ┌──────────▼──────────┐
     * │ Check Controllers   │
     * │ (@RequestMapping)   │
     * └──────────┬──────────┘
     * │ No match
     * ┌──────────▼──────────┐
     * │ Check Resource      │
     * │ Handlers            │
     * └──────────┬──────────┘
     * │ No match
     * ┌──────────▼──────────┐
     * │ Default Servlet     │◄── This config enables this
     * │ (Static files)      │
     * └─────────────────────┘
     * <p>
     * Example Scenarios:
     * <p>
     * Scenario 1: API Request
     * - Request: GET /api/trainees/John.Doe
     * - DispatcherServlet finds TraineeController
     * - Controller handles request
     * - Default servlet NOT used
     * <p>
     * Scenario 2: Swagger Resource
     * - Request: GET /swagger-ui.html
     * - DispatcherServlet checks controllers → no match
     * - Resource handlers match and serve it
     * - Default servlet NOT used
     * <p>
     * Scenario 3: Unknown Static File
     * - Request: GET /favicon.ico
     * - DispatcherServlet checks controllers → no match
     * - Resource handlers don't match
     * - Forwards to default servlet
     * - Default servlet looks in webapp/ directory
     * - If found → serves it; if not → 404
     * <p>
     * Why Enable It?
     * - Graceful fallback for unmatched requests
     * - Allows serving static files from webapp directory
     * - Prevents "No mapping found" errors for legitimate static resources
     * - Standard practice in Spring MVC applications
     * <p>
     * When NOT to Enable:
     * - Pure REST API with no static files
     * - Want strict 404 errors for all unmatched paths
     * - Custom 404 handling through @ControllerAdvice
     * <p>
     * Alternative Approach:
     * Instead of default servlet, could configure explicit resource handler:
     * <pre>
     * registry.addResourceHandler("/**")
     *         .addResourceLocations("/")
     *         .setCachePeriod(3600);
     * </pre>
     * <p>
     * Configuration Details:
     * - configurer.enable(): Turns on default servlet forwarding
     * - Uses servlet container's default servlet (e.g., Tomcat's DefaultServlet)
     * - Mapped to lowest priority (tried last)
     * - Respects servlet container's configuration
     *
     * @param configurer - DefaultServletHandlerConfigurer for enabling/disabling
     *                   default servlet handling
     * @see org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer
     * @see org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}