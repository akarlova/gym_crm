package com.epam.gym_crm.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.ServletRegistration;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{AppConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebConfig.class};
    }

    /**
     * Defines URL patterns that should be handled by the DispatcherServlet.
     * <p>
     * The DispatcherServlet is Spring's front controller that receives all HTTP
     * requests matching these patterns and delegates them to appropriate handlers
     * (controllers).
     * <p>
     * Mapping Options:
     * <p>
     * 1. "/" (Current configuration - RECOMMENDED)
     * - Handles ALL requests to the application
     * - Acts as a default servlet
     * - Allows Spring MVC to handle all routes
     * - Static resources can still be served via configuration
     * - Best for REST APIs and SPAs (Single Page Applications)
     * <p>
     * 2. "/api/*"
     * - Only handles requests starting with /api/
     * - Other requests bypass DispatcherServlet
     * - Useful when mixing Spring MVC with other frameworks
     * - Example: /api/trainees/register would be handled
     * /index.html would NOT be handled
     * <p>
     * 3. "*.do" or "*.action"
     * - Only handles requests with specific extensions
     * - Legacy pattern from older frameworks
     * - Not recommended for modern REST APIs
     * <p>
     * Why "/" for REST APIs?
     * - Clean URLs without extensions (/api/trainees not /api/trainees.do)
     * - Controller @RequestMapping defines the actual paths
     * - Follows RESTful design principles
     * - Consistent with modern web application practices
     * <p>
     * How It Works:
     * Request: GET http://localhost:8080/api/trainees/John.Doe
     * → Matches "/" pattern
     * → Routed to DispatcherServlet
     * → DispatcherServlet checks @RequestMapping in controllers
     * → Finds TraineeController with @GetMapping("/{username}")
     * → Executes the controller method
     *
     * @return Array of URL patterns to map to DispatcherServlet
     */
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    /**
     * Customizes the DispatcherServlet registration with additional parameters.
     * <p>
     * This method is called after the DispatcherServlet is registered but before
     * the application starts. It allows fine-tuning of servlet behavior through
     * initialization parameters.
     * <p>
     * Current Configuration:
     * <p>
     * dispatchOptionsRequest = "true"
     * --------------------------------
     * Enables the DispatcherServlet to handle HTTP OPTIONS requests.
     * <p>
     * What are OPTIONS requests?
     * - HTTP method used to describe communication options for a resource
     * - Commonly used in CORS (Cross-Origin Resource Sharing) preflight requests
     * - Browsers send OPTIONS before actual requests to check if cross-origin
     * requests are allowed
     * <p>
     * Why enable it?
     * - Required for CORS support in REST APIs
     * - Allows browsers from different origins to call your API
     * - Modern web applications (React, Angular, Vue) often run on different
     * ports during development (e.g., frontend on 3000, API on 8080)
     * - Enables proper REST API functionality for web clients
     * <p>
     * Example CORS Preflight Flow:
     * 1. Browser wants to make POST to http://localhost:8080/api/trainees/register
     * 2. Browser first sends: OPTIONS http://localhost:8080/api/trainees/register
     * 3. Server responds with allowed methods, headers, origins
     * 4. If allowed, browser proceeds with actual POST request
     * <p>
     * Other Common Init Parameters:
     * (Not currently used, but available if needed)
     * <p>
     * - throwExceptionIfNoHandlerFound: "true"
     * Throws NoHandlerFoundException instead of 404
     * Useful for custom 404 handling in @ControllerAdvice
     * <p>
     * - dispatchTraceRequest: "true"
     * Enables handling of HTTP TRACE requests (rarely used)
     * <p>
     * - enableLoggingRequestDetails: "true"
     * Logs detailed request information (form data, headers)
     * WARNING: May log sensitive data, use only in development
     * <p>
     * Example Usage:
     * <pre>
     * registration.setInitParameter("throwExceptionIfNoHandlerFound", "true");
     * registration.setInitParameter("enableLoggingRequestDetails", "false");
     * </pre>
     *
     * @param registration - Dynamic registration object for the DispatcherServlet
     *                     Provides methods to configure the servlet at runtime
     * @see javax.servlet.ServletRegistration.Dynamic
     * @see org.springframework.web.servlet.DispatcherServlet
     */
    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        registration.setInitParameter("dispatchOptionsRequest", "true");
    }
}