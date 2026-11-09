package user_service.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import user_service.service.CustomerUserDetailsService;
import user_service.service.JwtService;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomerUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomerUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token!=null && token.startsWith("Bearer ")){
            String rawToken = token.substring(7);
            try {
                DecodedJWT decodedJWT = jwtService.validateTokenAndFetchUser(rawToken);
                String email = decodedJWT.getSubject();
                if (SecurityContextHolder.getContext().getAuthentication() == null){
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    if (!userDetails.isEnabled()){
                        throw new DisabledException("User's Account is Deactivated!!");
                    }

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("AUTHENTICATED USER: " + email);
                    System.out.println("USER AUTHORITIES: " + userDetails.getAuthorities());
                }

            } catch (JWTVerificationException | UsernameNotFoundException | DisabledException exception) {
                exception.printStackTrace();
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
