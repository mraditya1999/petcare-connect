//package com.petconnect.backend.config;
//
//import com.petconnect.backend.interceptors.RequestInterceptor;
//import jakarta.servlet.MultipartConfigElement;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
//import org.springframework.boot.web.server.WebServerFactoryCustomizer;
//import org.springframework.boot.web.servlet.MultipartConfigFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//import org.springframework.data.web.config.EnableSpringDataWebSupport;
//import org.springframework.util.unit.DataSize;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Configuration
//@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
//public class WebConfig implements WebMvcConfigurer {
//
//    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);
//
//    private final RequestInterceptor requestInterceptor;
//    private final Environment env;
//
//    @Value("${frontend.urls}")
//    private String frontendUrls;
//
//    @Autowired
//    public WebConfig(RequestInterceptor requestInterceptor, Environment env) {
//        this.requestInterceptor = requestInterceptor;
//        this.env = env;
//        logger.info("WebConfig initialized with frontendUrls: {}", frontendUrls);
//    }
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(requestInterceptor)
//                .addPathPatterns("/auth/**", "/profiles/**", "/users/**", "/specialists/**", "/pets/**", "/forums/**", "/comments/**", "/likes/**", "/admin/**");
//        logger.info("Interceptors added");
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        if (frontendUrls != null && !frontendUrls.isEmpty()) {
//            List<String> allowedOrigins = Arrays.asList(frontendUrls.split(","));
//            logger.info("Allowed Origins: {}", allowedOrigins);
//
//            // Validate origins
//            for (String origin : allowedOrigins) {
//                if (!isValidURL(origin)) {
//                    throw new IllegalArgumentException("Invalid frontend URL: " + origin);
//                }
//            }
//            configuration.setAllowedOrigins(allowedOrigins);
//            configuration.setAllowCredentials(true); // Allow credentials
//
//        } else {
//            String message = "WARNING: No frontend URLs configured. CORS may not work as expected.";
//            if (env.acceptsProfiles("prod")) {
//                throw new IllegalStateException(message + " frontend.urls property MUST be configured in production!");
//            } else {
//                System.err.println(message + " Consider configuring frontend.urls for proper CORS in development.");
//            }
//        }
//
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//
//        List<String> allowedHeaders = Arrays.asList("Content-Type", "Authorization", "X-Requested-With");
//        configuration.setAllowedHeaders(allowedHeaders);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//
//    private boolean isValidURL(String url) {
//        try {
//            new java.net.URL(url).toURI();
//            return true;
//        } catch (Exception e) {
//            logger.error("Invalid URL: {}", url);
//            return false;
//        }
//    }
//
//    @Bean
//    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> webServerFactoryCustomizer() {
//        return factory -> {
//            factory.setContextPath("/api/v1");
//            logger.info("Customizing Tomcat context path to /api/v1");
//        };
//    }
//
//    @Bean
//    public MultipartConfigElement multipartConfigElement() {
//        MultipartConfigFactory factory = new MultipartConfigFactory();
//        factory.setMaxFileSize(DataSize.ofMegabytes(10));
//        factory.setMaxRequestSize(DataSize.ofMegabytes(20));
//
//        String uploadLocation = env.getProperty("upload.location");
//        factory.setLocation(uploadLocation != null ? uploadLocation : "/tmp/uploads");
//
//        logger.info("MultipartConfigElement configured with upload location: {}", uploadLocation);
//        return factory.createMultipartConfig();
//    }
//}



package com.petconnect.backend.config;

import com.petconnect.backend.interceptors.RequestInterceptor;
import com.petconnect.backend.utils.AppConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;
import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class WebConfig implements WebMvcConfigurer {

    private final RequestInterceptor interceptor;
    private final List<String> allowedOrigins;

    public WebConfig(RequestInterceptor interceptor,
                     @Value("${frontend.urls}") String urls) {

        this.interceptor = interceptor;
        this.allowedOrigins = Arrays.stream(urls.split(","))
                .map(String::trim)
                .toList();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns(AppConstants.PROTECTED_PATHS);
    }

    @Bean
    public CorsConfigurationSource corsConfig() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(allowedOrigins);
        config.setAllowCredentials(true);
        config.addAllowedHeader(CorsConfiguration.ALL);
        config.addAllowedMethod(CorsConfiguration.ALL);
        config.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins.toArray(new String[0]))
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowCredentials(true);
    }

}
