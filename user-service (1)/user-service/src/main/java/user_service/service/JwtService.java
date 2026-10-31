package user_service.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    private final static String SECRET_KEY="Sona7662@";
    private final static Long EXPIRATION_TIME=3600000L;

    public String generateToken(String email, String mobile, String role) {
        return JWT.create()
                .withSubject(email)
                .withClaim("mobile",mobile)
                .withClaim("role",role)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    public DecodedJWT validateTokenAndFetchUser(String Token){
        return JWT.require(Algorithm.HMAC256(SECRET_KEY))
                .build()
                .verify(Token);
    }
}
