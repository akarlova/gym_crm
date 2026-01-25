package com.epam.gym_crm.workload.web.exception;

import com.epam.gym_crm.workload.logging.TransactionIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    public record ApiError(String code, String message, String transactionId) {}

    private ResponseEntity<ApiError> build(HttpStatus status, String code, String message,
                                           HttpServletRequest req, HttpServletResponse res) {
        String tx = MDC.get(TransactionIdFilter.MDC_KEY);

        res.setHeader("X-Error-Code", code);
        res.setHeader("X-Error-Message",
                message != null && message.length() > 160 ? message.substring(0, 160) : String.valueOf(message));

        return ResponseEntity.status(status).body(new ApiError(code, message, tx));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handle401(UnauthorizedException ex,
                                              HttpServletRequest req, HttpServletResponse res) {
        return build(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", ex.getMessage(), req, res);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handle400(IllegalArgumentException ex,
                                              HttpServletRequest req, HttpServletResponse res) {
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), req, res);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handle500(Exception ex,
                                              HttpServletRequest req, HttpServletResponse res) {
        String msg = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", msg, req, res);
    }
}
