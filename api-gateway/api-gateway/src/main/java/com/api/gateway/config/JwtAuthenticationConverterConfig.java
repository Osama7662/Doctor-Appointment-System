package com.api.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;

@Configuration
public class JwtAuthenticationConverterConfig {

    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {

        ReactiveJwtAuthenticationConverter converter =
                new ReactiveJwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt ->
                reactor.core.publisher.Flux.just(
                        new SimpleGrantedAuthority(
                                "ROLE_" + jwt.getClaimAsString("role")
                        )
                )
        );

        return converter;
    }
}
