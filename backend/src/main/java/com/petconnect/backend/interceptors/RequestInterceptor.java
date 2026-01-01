package com.petconnect.backend.interceptors;

import io.github.resilience4j.core.lang.NonNull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Component
public class RequestInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws IOException {
        logger.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request, HttpServletResponse response, @NonNull Object handler, ModelAndView modelAndView) {
        logger.info("Outgoing response: {}", response.getStatus());
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        if (ex != null) {
            logger.error("Request completed with exception: ", ex);
        } else {
            logger.info("Request completed successfully");
        }
    }
}
