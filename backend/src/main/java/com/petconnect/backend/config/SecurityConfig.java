package com.petconnect.backend.config;
//package com.petconnect.backend.config;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//import static org.springframework.security.config.Customizer.withDefaults;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    // Bean for creating a PasswordEncoder instance
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests((requests) ->
//                        requests
//                                // Public routes for authentication-related operations
//                                .requestMatchers(
//                                        "/auth/login",
//                                        "/auth/register",
//                                        "/auth/forgot-password",
//                                        "/auth/reset-password",
//                                        "/auth/verify-account"
//                                ).permitAll() // Allow access to these routes without authentication
//                                // Admin route example (adjust as per your use case)
//                                .requestMatchers("/admin/**").hasRole("ADMIN")
//                                .requestMatchers("/maintenance").denyAll()
//                                // Protect all other routes (require authentication)
//                                .anyRequest().authenticated()
//                )
//                .httpBasic(withDefaults());
//
//        // Add logout handling (Spring provides a default logout implementation)
//        http.logout(logout ->
//                logout
//                        .logoutUrl("/auth/logout") // URL for logout
//                        .logoutSuccessUrl("/auth/login") // Redirect after logout
//                        .invalidateHttpSession(true) // Destroy session on logout
//                        .deleteCookies("JSESSIONID") // Remove session cookies
//        );
//
//        return http.build();
//    }
//}

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import java.util.Arrays;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.cors(withDefaults())
                .authorizeHttpRequests((requests) -> requests
//                .requestMatchers(
//                        "/api/v1/auth/login",
//                        "/api/v1/auth/register",
//                        "/api/v1/auth/forgot-password",
//                        "/api/v1/auth/reset-password",
//                        "/api/v1/auth/verify-account",
//                        "/api/v1/contact"
//                ).permitAll()
//                .requestMatchers("/api/v1/admin").denyAll()
                .requestMatchers("/auth/*").permitAll()
                .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173")); // Modify this to match your frontend's URL
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
