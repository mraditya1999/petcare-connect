//package com.petconnect.backend.security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import static org.springframework.security.config.Customizer.withDefaults;
//
//@EnableWebSecurity
//@Configuration
//public class SecurityConfig {
//
//    private final JwtRequestFilter jwtRequestFilter;
//    private final UserDetailsServiceImpl userDetailsService;
//
//    @Autowired
//    public SecurityConfig(JwtRequestFilter jwtRequestFilter, UserDetailsServiceImpl userDetailsService) {
//        this.jwtRequestFilter = jwtRequestFilter;
//        this.userDetailsService = userDetailsService;
//    }
//
//    private static final String[] AUTH_WHITELIST = {
//            "/auth/**",
//            "/forums/**",
//            "/appointments/**",
//    };
//
//    @Bean
//    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
//        http.cors(withDefaults())
//                .authorizeHttpRequests((requests) -> requests
//                        .requestMatchers(AUTH_WHITELIST).permitAll()
//                        .requestMatchers("/profile/**").authenticated()
/// /                        .requestMatchers("/forums/create", "/forums/update/**", "/forums/delete/**").authenticated()
/// /                        .requestMatchers("/comments/**").authenticated()
/// /                        .requestMatchers("/likes/**").authenticated()
/// /                        .requestMatchers("/profile/users/**", "/profile/users/role/**").hasRole("ADMIN")
//                        .anyRequest().authenticated())
//                .csrf(AbstractHttpConfigurer::disable)
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
//        http.httpBasic(withDefaults());
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
//        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
//        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
//        return auth.build();
//    }
//}
//
package com.petconnect.backend.security;

//import com.petconnect.backend.entity.Appointment;
import com.petconnect.backend.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final AuthService authService;

    @Autowired
    public SecurityConfig(JwtRequestFilter jwtRequestFilter, AuthService authService) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.authService = authService;
    }

    private static final String[] AUTH_WHITELIST = {
            "/auth/**",
            "/upload/**",
    };

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.cors(withDefaults())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .requestMatchers(HttpMethod.GET,"/forums/**","/specialists/**").permitAll()
                        .requestMatchers("/profile/**","/forums/**","/appointments/**","/pets/**").authenticated()
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        http.httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.userDetailsService(authService).passwordEncoder(passwordEncoder());
        return auth.build();
    }

//    In-memory databases
//    @Bean
//    public UserDetailsService userDetailsService() {
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        if (!manager.userExists("ay5480620@gmail.com")) {
//            manager.createUser(User.withUsername("ay5480620@gmail.com").password("@mrAditya1999").roles("ADMIN").build());
//        }
//
//        if (!manager.userExists("dbadaditya@gmail.com")) {
//            manager.createUser(User.withUsername("dbadaditya@gmail.com").password("@mrAditya1999").roles("USER").build());
//        }
//        return manager;
//    }

//    Jdbc databases
//    @Bean
//    public UserDetailsService userDetailsService(DataSource dataSource) {
//        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
//        if (!manager.userExists("ay5480620@gmail.com")) {
//            manager.createUser(User.withUsername("ay5480620@gmail.com").password("@mrAditya1999").roles("ADMIN").build());
//        }
//
//        if (!manager.userExists("dbadaditya@gmail.com")) {
//            manager.createUser(User.withUsername("dbadaditya@gmail.com").password("@mrAditya1999").roles("USER").build());
//        }
//        return manager;
//    }
}
