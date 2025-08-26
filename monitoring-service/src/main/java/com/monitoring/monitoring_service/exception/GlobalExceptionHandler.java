package com.monitoring.monitoring_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * {@code GlobalExceptionHandler} provides centralized exception handling
 * for all REST controllers in the monitoring service.
 * <p>
 * By using {@link RestControllerAdvice}, it ensures that all exceptions
 * are caught in one place, logged appropriately, and returned as a
 * consistent JSON error response.
 *
 * <p>Each exception handler maps specific exception types
 * to corresponding HTTP status codes:
 * <ul>
 *   <li>400 - Bad Request (invalid input, missing params, validation errors)</li>
 *   <li>404 - Not Found (entity not found, no such element)</li>
 *   <li>403 - Forbidden (access denied)</li>
 *   <li>503 - Service Unavailable (database errors)</li>
 *   <li>422 - Unprocessable Entity (custom domain errors)</li>
 *   <li>500 - Internal Server Error (any other uncaught exception)</li>
 * </ul>
 *
 * @since Aug, 2025
 * @author s Bostan
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Build a standard error response map used by all handlers.
     *
     * @param status  HTTP status to be returned
     * @param message Error message to show in response
     * @param request Current web request, used to extract URI path
     * @return A {@link Map} containing error details
     */
    private Map<String, Object> buildResponse(HttpStatus status, String message, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());
        return body;
    }

    // ---------------- 400 BAD REQUEST ----------------

    /**
     * Handle cases where request parameters are invalid or missing.
     * <ul>
     *   <li>{@link IllegalArgumentException} → thrown when an invalid argument is passed</li>
     *   <li>{@link MissingServletRequestParameterException} → when a required query parameter is missing</li>
     * </ul>
     *
     * @param ex the thrown exception
     * @param request current request
     * @return JSON response with status 400
     */
    @ExceptionHandler({IllegalArgumentException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequest(Exception ex, WebRequest request) {
        log.warn("Bad request: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle validation errors when DTO fields fail validation annotations.
     * <p>
     * Example: {@code @NotNull}, {@code @Size}, {@code @Email}.
     * When violated, Spring throws {@link MethodArgumentNotValidException}.
     *
     * @param ex the validation exception with binding result
     * @param request current request
     * @return JSON response with status 400 containing validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        log.warn("Validation error: {}", message, ex);

        return new ResponseEntity<>(buildResponse(HttpStatus.BAD_REQUEST, message, request), HttpStatus.BAD_REQUEST);
    }

    // ---------------- 404 NOT FOUND ----------------

    /**
     * Handle cases where requested entity/resource is not found.
     * <ul>
     *   <li>{@link EntityNotFoundException} → thrown by JPA when entity doesn't exist</li>
     *   <li>{@link NoSuchElementException} → thrown when an Optional is empty but accessed</li>
     * </ul>
     *
     * @param ex the exception
     * @param request current request
     * @return JSON response with status 404
     */
    @ExceptionHandler({EntityNotFoundException.class, NoSuchElementException.class})
    public ResponseEntity<Map<String, Object>> handleNotFound(Exception ex, WebRequest request) {
        log.info("Resource not found: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request), HttpStatus.NOT_FOUND);
    }

    // ---------------- 403 FORBIDDEN ----------------

    /**
     * Handle access denied errors (insufficient permissions).
     * <p>
     * Typically thrown by Spring Security as {@link AccessDeniedException}.
     *
     * @param ex the exception
     * @param request current request
     * @return JSON response with status 403
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        log.warn("Access denied: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(buildResponse(HttpStatus.FORBIDDEN, "Access denied", request), HttpStatus.FORBIDDEN);
    }

    // ---------------- 503 SERVICE UNAVAILABLE ----------------

    /**
     * Handle database-related errors.
     * <p>
     * Typically thrown as {@link DataAccessException} when there are database
     * connectivity or query execution issues.
     *
     * @param ex the database exception
     * @param request current request
     * @return JSON response with status 503
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDatabaseError(DataAccessException ex, WebRequest request) {
        log.error("Database error: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "Database error", request), HttpStatus.SERVICE_UNAVAILABLE);
    }

    // ---------------- 422 UNPROCESSABLE ENTITY ----------------

    /**
     * Handle custom domain/business logic exceptions.
     * <p>
     * {@code CustomMonitoringException} can be used for cases like
     * invalid monitoring rules, thresholds, or configuration conflicts.
     *
     * @param ex the custom monitoring exception
     * @param request current request
     * @return JSON response with status 422
     */
    @ExceptionHandler(CustomMonitoringException.class)
    public ResponseEntity<Map<String, Object>> handleCustomMonitoringException(CustomMonitoringException ex, WebRequest request) {
        log.error("Custom monitoring error: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    // ---------------- 500 INTERNAL SERVER ERROR ----------------

    /**
     * Fallback handler for all uncaught exceptions.
     * <p>
     * Catches any exception not handled by other methods
     * and returns a generic {@code 500 Internal Server Error}.
     *
     * @param ex the uncaught exception
     * @param request current request
     * @return JSON response with status 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex, WebRequest request) {
        log.error("Unhandled exception occurred: {}" , ex.getMessage(), ex);
        return new ResponseEntity<>(buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
