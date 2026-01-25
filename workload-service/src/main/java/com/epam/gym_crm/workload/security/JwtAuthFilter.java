package com.epam.gym_crm.workload.security;

import com.epam.gym_crm.workload.service.IntegrationJwtService;
import com.epam.gym_crm.workload.web.exception.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final IntegrationJwtService jwtService;
    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final HandlerExceptionResolver resolver;

    public JwtAuthFilter(
            IntegrationJwtService jwtService,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver
    ) {
        this.jwtService = jwtService;
        this.resolver = resolver;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        boolean skip = path.startsWith("/actuator/")
                       || path.startsWith("/h2-console")
                       || path.startsWith("/h2-console/");
        log.info("shouldNotFilter? {} -> {}", path, skip);
        return skip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtAuthFilter hit: {} {}", request.getMethod(), request.getRequestURI());

        try {
            String auth = request.getHeader("Authorization");
            if (auth == null || !auth.startsWith("Bearer ")) {
                throw new UnauthorizedException("Missing Bearer token");
            }

            String token = auth.substring("Bearer ".length());
            if (!jwtService.isTokenValid(token)) {
                throw new UnauthorizedException("Invalid token");
            }

            String subject = jwtService.extractSubject(token);
            if (!"gym-crm".equals(subject)) {
                throw new UnauthorizedException("Invalid token subject");
            }

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken("gym-crm", null, List.of())
            );

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            resolver.resolveException(request, response, null, ex);
        }
    }
}
