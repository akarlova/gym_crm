package com.epam.gym_crm.web.interceptor;

import com.epam.gym_crm.service.IAuthService;
import com.epam.gym_crm.web.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);
    private final IAuthService authService;

    public AuthInterceptor(IAuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String path = request.getServletPath();

        if ("/trainees".equals(path) && "POST".equals(request.getMethod())) return true;
        if ("/trainers".equals(path) && "POST".equals(request.getMethod())) return true;
        if ("/users/login".equals(path) && "GET".equals(request.getMethod())) return true;
        if ("/users/ping".equals(path)  && "GET".equals(request.getMethod())) return true;
        if (path.startsWith("/v3/api-docs")) return true;
        if (path.startsWith("/swagger-ui"))  return true;
        if (path.startsWith("/favicon"))  return true;

        String username = request.getHeader("X-Username");
        String password = request.getHeader("X-Password");

        if (username == null || password == null) {
            log.warn("Auth failed: missing headers");
            throw new UnauthorizedException("Missing credentials");
        }


        boolean ok = authService.verifyTrainee(username, password)
                     || authService.verifyTrainer(username, password);
        if (!ok) {
            log.warn("Auth failed: invalid credentials for {}", username);
            throw new UnauthorizedException("Invalid credentials");
        }
        return true;
    }
}
