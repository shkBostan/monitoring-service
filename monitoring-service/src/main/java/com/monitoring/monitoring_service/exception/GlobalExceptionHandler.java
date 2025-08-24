package com.monitoring.monitoring_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler handles exceptions thrown by all Controllers in a centralized manner.
 * <p>
 * It logs the exception and returns a standardized JSON response with details such as
 * timestamp, HTTP status, error, message, and the request path.
 *
 * <p>This improves traceability, monitoring, and debugging in production environments.
 *
 * @since Aug, 2025
 * @author s Bostan
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle all uncaught exceptions globally.
     * Logs the exception and returns a standard JSON response with HTTP 500.
     *
     * @param ex      the exception thrown
     * @param request the current web request
     * @return a ResponseEntity containing the JSON error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex, WebRequest request) {
        // ERROR: log the exception with stack trace
        log.error("Unhandled exception occurred: {}", ex.getMessage(), ex);

        // Build a standardized JSON response body
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", ex.getMessage());
        body.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle IllegalArgumentException specifically.
     * Logs the exception and returns a standard JSON response with HTTP 400.
     *
     * @param ex      the IllegalArgumentException thrown
     * @param request the current web request
     * @return a ResponseEntity containing the JSON error response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.warn("Illegal argument exception: {}", ex.getMessage(), ex);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        body.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // TODO: add more exception handlers as needed (e.g., NotFoundException, CustomException)
}
