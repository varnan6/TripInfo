package com.tripinfo.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple in-memory rate limiter: max 100 requests per IP per minute.
 *
 * Design notes:
 * - Uses a ConcurrentHashMap keyed by IP address — no external library needed.
 * - A background thread resets all counters every 60 seconds.
 * - Responds with HTTP 429 Too Many Requests when the limit is exceeded.
 * - Applies to all /api/** routes automatically (registered as a Servlet Filter).
 */
@Component
public class RateLimitFilter implements Filter {

    private static final int MAX_REQUESTS_PER_MINUTE = 100;

    // IP -> request count in the current window
    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();

    public RateLimitFilter() {
        // Background thread: reset all counters every 60 seconds
        Thread resetter = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(60_000);
                    requestCounts.clear();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        resetter.setDaemon(true);   // dies when the JVM shuts down
        resetter.setName("rate-limit-resetter");
        resetter.start();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  httpReq  = (HttpServletRequest)  request;
        HttpServletResponse httpResp = (HttpServletResponse) response;

        String ip = httpReq.getRemoteAddr();
        int count = requestCounts
                .computeIfAbsent(ip, k -> new AtomicInteger(0))
                .incrementAndGet();

        if (count > MAX_REQUESTS_PER_MINUTE) {
            httpResp.setStatus(429);
            httpResp.setContentType("application/json");
            httpResp.getWriter().write(
                "{\"error\": \"Too many requests. Limit: "
                + MAX_REQUESTS_PER_MINUTE + " per minute.\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}
