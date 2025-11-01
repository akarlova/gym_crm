package com.epam.gym_crm.web.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    public record ApiError(String code, String message, String transactionId) {
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String code, String message,
                                           HttpServletRequest req, HttpServletResponse res) {
        String tx = MDC.get("transactionId");
        res.setHeader("X-Error-Code", code);
        res.setHeader("X-Error-Message", message != null && message.length()>160 ? message.substring(0,160) : String.valueOf(message));
        return ResponseEntity.status(status).body(new ApiError(code, message, tx));
    }

    // 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
                                                     HttpServletRequest req, HttpServletResponse res) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", msg, req, res);
    }

    // 401
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handle401(UnauthorizedException ex,
                                              HttpServletRequest req, HttpServletResponse res) {
        return build(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", ex.getMessage(), req, res);
    }

    // 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex,
                                                     HttpServletRequest req, HttpServletResponse res) {
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), req, res);
    }

    // 400
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(ConstraintViolationException ex,
                                                     HttpServletRequest req, HttpServletResponse res) {
        String msg = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, "CONSTRAINT_VIOLATION", msg, req, res);
    }

    // 400
    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiError> handleBadParams(Exception ex,
                                                    HttpServletRequest req, HttpServletResponse res) {
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), req, res);
    }

    // 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleJson(HttpMessageNotReadableException ex,
                                               HttpServletRequest req, HttpServletResponse res) {
        String msg = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : ex.getMessage();
        return build(HttpStatus.BAD_REQUEST, "MALFORMED_JSON", msg, req, res);
    }

    // 404
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> handle404(NoHandlerFoundException ex,
                                              HttpServletRequest req, HttpServletResponse res) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getRequestURL(), req, res);
    }

    // 409 — database
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleConflict(DataIntegrityViolationException ex,
                                                   HttpServletRequest req, HttpServletResponse res) {
        String msg = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : ex.getMessage();
        return build(HttpStatus.CONFLICT, "DATA_INTEGRITY_VIOLATION", msg, req, res);
    }

    // 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(Exception ex,
                                              HttpServletRequest req, HttpServletResponse res) {
        String msg = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", msg, req, res);
    }
}