package com.epam.gym_crm.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger REST_LOG = LoggerFactory.getLogger("REST");
    private static final String HEADER = "X-Transaction-Id";
    private static final String MDC_KEY = "transactionId";

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        long started = System.currentTimeMillis();

        String txId = req.getHeader(HEADER);
        if (txId == null || txId.isBlank()) {
            txId = UUID.randomUUID().toString().substring(0, 8);
        }

        MDC.put(MDC_KEY, txId);
        res.setHeader(HEADER, txId);

        try {
            String method = req.getMethod();
            String uri = req.getRequestURI();
            String q = req.getQueryString();
            REST_LOG.info("[IN ] tx={} {} {}{}", txId, method, uri, (q == null ? "" : "?" + q));
            chain.doFilter(req, res);
        } finally {
            long ms = System.currentTimeMillis() - started;
            String code = res.getHeader("X-Error-Code");
            String emsg = res.getHeader("X-Error-Message");

            if (code != null) {
                REST_LOG.info("[OUT] tx={} {} {} -> {} ({} ms) code={} msg={}",
                        txId, req.getMethod(), req.getRequestURI(), res.getStatus(), ms, code, emsg);
            } else {
                REST_LOG.info("[OUT] tx={} {} {} -> {} ({} ms)",
                        txId, req.getMethod(), req.getRequestURI(), res.getStatus(), ms);
            }
            MDC.remove(MDC_KEY);
        }
    }
}
