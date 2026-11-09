package com.doctor_service.config;

import com.doctor_service.filter.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
        throws Exception{
        http
                .csrf(csrf ->csrf.disable())
                .sessionManagement(
                        session ->
                                session.sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS
                                )
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("text/plain");
                            response.getWriter().write("Authentication Required");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("text/plain");
                            response.getWriter().write("Access Denied");
                        })
                )
                .authorizeHttpRequests(
                        req -> req
                                // Doctor registration
                                .requestMatchers("/api/v1/doctors/register")
                                .hasRole("DOCTOR")

                                // Read doctor profile
                                .requestMatchers(
                                        "/api/v1/doctors/read/**",
                                        "/api/v1/doctors/user/**",
                                        "/api/v1/doctors/getAll"
                                )
                                .hasAnyRole("PATIENT","DOCTOR", "ADMIN")

                                // Update doctor profile
                                .requestMatchers("/api/v1/doctors/update/**")
                                .hasAnyRole("DOCTOR", "ADMIN")

                                // Delete doctor profile
                                .requestMatchers("/api/v1/doctors/delete/**")
                                .hasAnyRole("DOCTOR", "ADMIN")

                                .anyRequest()
                                .authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }

}
