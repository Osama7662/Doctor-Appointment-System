
package com.appointment_service.config;

import com.appointment_service.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        ))

                .authorizeHttpRequests(req ->
                        req

                                // PATIENT endpoints
                                .requestMatchers(HttpMethod.POST, "/api/appointment/book")
                                .hasRole("PATIENT")

                                .requestMatchers(HttpMethod.GET, "/api/appointment/patient/**")
                                .hasAnyRole("PATIENT", "ADMIN")

                                .requestMatchers(HttpMethod.DELETE, "/api/appointment/cancel/**")
                                .hasRole("PATIENT")


                                // DOCTOR endpoints
                                .requestMatchers(HttpMethod.GET, "/api/appointment/doctor/**")
                                .hasAnyRole("DOCTOR", "ADMIN")


                                // ADMIN endpoints
                                .requestMatchers("/api/appointment/admin/**")
                                .hasRole("ADMIN")

                                .requestMatchers(HttpMethod.GET, "/api/appointment/getAll")
                                .hasRole("ADMIN")

                                .requestMatchers(HttpMethod.GET, "/api/appointment/get/**")
                                .hasRole("ADMIN")


                                // Other endpoints require authentication
                                .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
