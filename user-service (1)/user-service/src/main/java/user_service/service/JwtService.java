package user_service.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;
    private final static Long EXPIRATION_TIME=3600000L;

    public String generateToken(Long userID, String email, String mobile, String role) {
        return JWT.create()
                .withSubject(email)
                .withClaim("userId", userID)
                .withClaim("mobile",mobile)
                .withClaim("role",role)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(secretKey));
    }

    public DecodedJWT validateTokenAndFetchUser(String Token){
        return JWT.require(Algorithm.HMAC256(secretKey))
                .build()
                .verify(Token);
    }
}
