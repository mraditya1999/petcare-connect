package com.petconnect.backend.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple in-memory rate limiting filter.
 * Applies a per-IP sliding-window limit for sensitive endpoints (auth, upload, OTP).
 * This is intentionally lightweight; switch to a distributed limiter (Redis/Bucket4j) for clustered deployments.
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

    private static final Set<String> SENSITIVE_PATH_PARTS = Set.of("/auth", "/login", "/register", "/upload", "/send-otp", "/verify-otp", "/forget-password", "/reset-password");

    // window size in milliseconds (1 minute)
    private static final long WINDOW_MS = 60_000L;
    // max requests per window per IP
    private static final int MAX_REQUESTS = 10;

    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // only filter sensitive paths
        return SENSITIVE_PATH_PARTS.stream().noneMatch(path::contains);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String clientKey = extractClientKey(request);
        long now = Instant.now().toEpochMilli();

        WindowCounter counter = counters.computeIfAbsent(clientKey, k -> new WindowCounter(now, new AtomicInteger(0)));

        synchronized (counter) {
            if (now - counter.windowStart >= WINDOW_MS) {
                counter.windowStart = now;
                counter.count.set(0);
            }

            int current = counter.count.incrementAndGet();
            if (current > MAX_REQUESTS) {
                logger.warn("Rate limit exceeded for {} on {}", clientKey, request.getRequestURI());
                response.setStatus(429);
                response.setContentType("application/json");
                String body = String.format("{\"error\":\"Too many requests. Try again after %d seconds.\"}", (WINDOW_MS - (now - counter.windowStart)) / 1000);
                response.getWriter().write(body);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractClientKey(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static class WindowCounter {
        volatile long windowStart;
        final AtomicInteger count;

        WindowCounter(long windowStart, AtomicInteger count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}
