package com.api.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            ReactiveJwtAuthenticationConverter jwtAuthenticationConverter) {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(
                                "/api/u1/user/signup",
                                "/api/u1/user/login"
                        ).permitAll()

                        .pathMatchers("/api/u1/admin/**")
                        .hasRole("ADMIN")

                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(
                                        jwtAuthenticationConverter
                                )
                        )
                );

        return http.build();
    }
}
