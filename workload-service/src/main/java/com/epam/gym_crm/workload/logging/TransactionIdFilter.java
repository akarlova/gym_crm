package com.epam.gym_crm.workload.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(org.springframework.core.Ordered.HIGHEST_PRECEDENCE)
public class TransactionIdFilter extends OncePerRequestFilter {
    public static final String HEADER = "X-Transaction-Id";
    public static final String MDC_KEY = "transactionId";

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String tx = req.getHeader(HEADER);
        if (tx == null || tx.isBlank()) {
            tx = UUID.randomUUID().toString().substring(0, 8);
        }

        MDC.put(MDC_KEY, tx);
        res.setHeader(HEADER, tx);

        try {
            chain.doFilter(req, res);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }
}
