package com.monitoring.monitoring_service.logging;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * RequestLoggingFilter adds a unique traceId for each incoming request
 * into the MDC (Mapped Diagnostic Context).
 *
 * This traceId will appear in all logs for the current request,
 * making debugging and tracing much easier across distributed systems.
 *
 * @since Aug, 2025
 * author s Bostan
 */
@Component
public class RequestLoggingFilter implements Filter {

    private static final String TRACE_ID = "traceId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            // generate unique traceId for each request
            String traceId = UUID.randomUUID().toString();
            MDC.put(TRACE_ID, traceId);

            if (request instanceof HttpServletRequest httpRequest) {
                String path = httpRequest.getRequestURI();
                String method = httpRequest.getMethod();
                // log contextual info
                System.out.printf("Incoming request: %s %s, traceId=%s%n", method, path, traceId);
            }

            // continue request chain
            chain.doFilter(request, response);

        } finally {
            // clean up MDC after request completes
            MDC.remove(TRACE_ID);
        }
    }
}
