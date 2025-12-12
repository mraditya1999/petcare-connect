package com.petconnect.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * Sets Cross-Origin-Opener-Policy (COOP) and Cross-Origin-Embedder-Policy (COEP) headers
 * to protect against Spectre/Meltdown vulnerabilities and enable SharedArrayBuffer access.
 * - COOP allows windows to be opened with popups while isolating the browsing context
 * - COEP requires all cross-origin resources to explicitly allow being embedded
 * See: https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cross-Origin-Opener-Policy
 */
@Component
public class COOPHeadersFilter extends OncePerRequestFilter {

    private static final String COOP_HEADER = "Cross-Origin-Opener-Policy";
    private static final String COOP_VALUE = "same-origin-allow-popups";
    private static final String COEP_HEADER = "Cross-Origin-Embedder-Policy";
    private static final String COEP_VALUE = "require-corp";

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        response.setHeader(COOP_HEADER, COOP_VALUE);
        response.setHeader(COEP_HEADER, COEP_VALUE);

        filterChain.doFilter(request, response);
    }
}

