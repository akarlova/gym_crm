package com.epam.gym_crm.workload.web.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger REST_LOG = LoggerFactory.getLogger("REST");

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        long started = System.currentTimeMillis();
        String txId = MDC.get("transactionId"); // уже положил TransactionIdFilter

        try {
            String method = req.getMethod();
            String uri = req.getRequestURI();
            String q = req.getQueryString();

            REST_LOG.info("[IN ] tx={} {} {}{}", txId, method, uri, (q == null ? "" : "?" + q));
            chain.doFilter(req, res);
        } finally {
            long ms = System.currentTimeMillis() - started;
            REST_LOG.info("[OUT] tx={} {} {} -> {} ({} ms)",
                    txId, req.getMethod(), req.getRequestURI(), res.getStatus(), ms);
        }
    }
}
