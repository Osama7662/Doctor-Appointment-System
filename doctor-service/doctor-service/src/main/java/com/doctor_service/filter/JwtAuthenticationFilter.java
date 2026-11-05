package com.doctor_service.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.doctor_service.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Service
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")){
            String rawToken = token.substring(7);
            try {
                DecodedJWT decodedJWT =
                        jwtService.validateToken(rawToken);
                Long userId =
                        decodedJWT.getClaim("userId").asLong();
                String role =
                        decodedJWT.getClaim("role").asString();
                if (userId != null && role != null){
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                            "ROLE_" + role.toUpperCase()
                    );
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            Collections.singleton(authority)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception exception){
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request,response);
    }
}
