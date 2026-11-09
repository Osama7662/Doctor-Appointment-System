package com.appointment_service.filter;

import com.appointment_service.service.JwtService;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
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
                DecodedJWT decodedJWT = jwtService.validateTokenAndFetchUser(rawToken);
                String email = decodedJWT.getSubject();
                Long userId = decodedJWT.getClaim("userId").asLong();
                String role = decodedJWT.getClaim("role").asString();

                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_"+role);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userId,
                        token,
                        List.of(authority)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (Exception exception){
                SecurityContextHolder.clearContext();

                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid Or Expired Authentication!!"
                );

                return;
            }
        }
        filterChain.doFilter(request,response);
    }
}
