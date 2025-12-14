package com.petconnect.backend.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Configuration for WebClient - Spring's reactive HTTP client.
 * Replaces RestTemplate with a more modern, non-blocking, and robust solution.
 * 
 * Features:
 * - Connection pooling for better performance
 * - Configurable timeouts (connect, read, write)
 * - Automatic retry with exponential backoff
 * - Request/response logging
 * - Error handling
 */
@Configuration
public class WebClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(WebClientConfig.class);

    // Timeout configurations
    private static final int CONNECT_TIMEOUT_SECONDS = 5;
    private static final int READ_TIMEOUT_SECONDS = 10;
    private static final int WRITE_TIMEOUT_SECONDS = 10;
    
    // Connection pool configurations
    private static final int MAX_CONNECTIONS = 500;
    private static final int MAX_IDLE_TIME_SECONDS = 20;
    private static final int MAX_LIFE_TIME_SECONDS = 60;
    private static final int PENDING_ACQUIRE_TIMEOUT_SECONDS = 45;
    
    // Retry configurations
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final Duration INITIAL_RETRY_DELAY = Duration.ofMillis(500);
    private static final double RETRY_MULTIPLIER = 2.0;
    private static final Duration MAX_RETRY_DELAY = Duration.ofSeconds(5);

    /**
     * Creates a connection provider with connection pooling for better performance.
     */
    @Bean
    public ConnectionProvider connectionProvider() {
        return ConnectionProvider.builder("webclient-pool")
                .maxConnections(MAX_CONNECTIONS)
                .maxIdleTime(Duration.ofSeconds(MAX_IDLE_TIME_SECONDS))
                .maxLifeTime(Duration.ofSeconds(MAX_LIFE_TIME_SECONDS))
                .pendingAcquireTimeout(Duration.ofSeconds(PENDING_ACQUIRE_TIMEOUT_SECONDS))
                .evictInBackground(Duration.ofSeconds(20))
                .build();
    }

    /**
     * Creates an HTTP client with timeouts and connection pooling.
     */
    @Bean
    public HttpClient httpClient(ConnectionProvider connectionProvider) {
        return HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_SECONDS * 1000)
                .responseTimeout(Duration.ofSeconds(READ_TIMEOUT_SECONDS))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS))
                );
    }

    /**
     * Logging filter for request/response debugging.
     */
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            if (logger.isDebugEnabled()) {
                logger.debug("Request: {} {}", clientRequest.method(), clientRequest.url());
                clientRequest.headers().forEach((name, values) -> 
                    values.forEach(value -> logger.debug("{}={}", name, value))
                );
            }
            return Mono.just(clientRequest);
        });
    }

    /**
     * Logging filter for response debugging.
     */
    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (logger.isDebugEnabled()) {
                logger.debug("Response status: {}", clientResponse.statusCode());
            }
            return Mono.just(clientResponse);
        });
    }

    /**
     * Error handling filter that logs errors appropriately.
     * Note: Actual error handling is done via onStatus in WebClient builder.
     * This filter just logs error responses.
     */
    private ExchangeFilterFunction handleErrors() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                logger.error("HTTP error response: {}", clientResponse.statusCode());
                // Return the response as-is - WebClient will handle the error via onStatus
                return Mono.just(clientResponse);
            }
            return Mono.just(clientResponse);
        });
    }

    /**
     * Retry filter with exponential backoff for transient failures.
     */
    private ExchangeFilterFunction retryFilter() {
        return (request, next) -> next.exchange(request)
                .retryWhen(Retry.backoff(MAX_RETRY_ATTEMPTS, INITIAL_RETRY_DELAY)
                        .maxBackoff(MAX_RETRY_DELAY)
                        .multiplier(RETRY_MULTIPLIER)
                        .filter(throwable -> {
                            // Only retry on network errors and 5xx server errors
                            if (throwable instanceof java.net.ConnectException ||
                                throwable instanceof java.net.SocketTimeoutException ||
                                throwable instanceof java.util.concurrent.TimeoutException) {
                                logger.warn("Retrying due to network error: {}", throwable.getMessage());
                                return true;
                            }
                            // Check for 5xx errors
                            if (throwable instanceof org.springframework.web.reactive.function.client.WebClientResponseException ex) {
                                if (ex.getStatusCode().is5xxServerError()) {
                                    logger.warn("Retrying due to server error {}: {}", 
                                            ex.getStatusCode(), ex.getMessage());
                                    return true;
                                }
                            }
                            return false;
                        })
                        .doBeforeRetry(retrySignal -> 
                            logger.debug("Retrying request (attempt {})", retrySignal.totalRetries() + 1)
                        )
                );
    }

    /**
     * Main WebClient bean with all configurations applied.
     * This is the primary WebClient instance for the application.
     */
    @Bean
    public WebClient webClient(HttpClient httpClient) {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filter(logRequest())
                .filter(logResponse())
                .filter(handleErrors())
                .filter(retryFilter())
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024)) // 10MB max buffer
                .build();
    }
}

