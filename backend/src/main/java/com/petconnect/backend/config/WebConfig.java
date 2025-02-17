package com.petconnect.backend.config;

import com.petconnect.backend.interceptors.RequestInterceptor;
import jakarta.servlet.MultipartConfigElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.util.unit.DataSize;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WebConfig implements WebMvcConfigurer {

    private final RequestInterceptor requestInterceptor;
    private final Environment env;

    @Value("${frontend.urls}")
    private String frontendUrls;

    @Autowired
    public WebConfig(RequestInterceptor requestInterceptor, Environment env) {
        this.requestInterceptor = requestInterceptor;
        this.env = env;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestInterceptor)
                .addPathPatterns("/auth/**", "/profiles/**", "/users/**", "/specialists/**", "/pets/**", "/forums/**", "/comments/**", "/likes/**", "/admin/**");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        if (frontendUrls != null && !frontendUrls.isEmpty()) {
            List<String> allowedOrigins = Arrays.asList(frontendUrls.split(","));

            // Validate origins
            for (String origin : allowedOrigins) {
                if (!isValidURL(origin)) {
                    throw new IllegalArgumentException("Invalid frontend URL: " + origin);
                }
            }
            configuration.setAllowedOrigins(allowedOrigins);

        } else {
            String message = "WARNING: No frontend URLs configured. CORS may not work as expected.";
            if (env.acceptsProfiles("prod")) {
                throw new IllegalStateException(message + " frontend.urls property MUST be configured in production!");
            } else {
                System.err.println(message + " Consider configuring frontend.urls for proper CORS in development.");
            }
        }

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Be as specific as possible

        List<String> allowedHeaders = Arrays.asList("Content-Type", "Authorization", "X-Requested-With"); // Customize!
        configuration.setAllowedHeaders(allowedHeaders);

        configuration.setAllowCredentials(false); // Set to true ONLY if you need it AND origins are specific!

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private boolean isValidURL(String url) {
        try {
            new java.net.URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> webServerFactoryCustomizer() {
        return factory -> factory.setContextPath("/api/v1");
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(10));
        factory.setMaxRequestSize(DataSize.ofMegabytes(20));

        String uploadLocation = env.getProperty("upload.location");
        factory.setLocation(uploadLocation != null ? uploadLocation : "/tmp/uploads"); // Production-safe location

        return factory.createMultipartConfig();
    }
}