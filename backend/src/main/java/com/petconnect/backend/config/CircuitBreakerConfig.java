package com.petconnect.backend.config;

import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CircuitBreakerConfig {

    @Bean
    public Customizer<org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory>
    defaultCircuitBreakerCustomizer() {

        io.github.resilience4j.circuitbreaker.CircuitBreakerConfig config = io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()

                // ❗ Failure rate threshold
                .failureRateThreshold(50)

                // ❗ Minimum calls before calculating failure rate
                .minimumNumberOfCalls(10)

                // ❗ Sliding window
                .slidingWindowType(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(20)

                // ❗ Slow call detection
                .slowCallRateThreshold(50)
                .slowCallDurationThreshold(Duration.ofSeconds(2))

                // ❗ OPEN → HALF_OPEN → CLOSED behavior
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .permittedNumberOfCallsInHalfOpenState(3)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)

                // ❗ Monitoring
                .recordExceptions(Throwable.class)

                .build();

        return factory ->
                factory.configureDefault(id ->
                        new Resilience4JConfigBuilder(id)
                                .circuitBreakerConfig(config)
                                .build()
                );
    }
}
