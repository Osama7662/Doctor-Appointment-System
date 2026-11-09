package com.appointment_service.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    public DecodedJWT validateTokenAndFetchUser(String Token){
        return JWT.require(Algorithm.HMAC256(secretKey))
                .build()
                .verify(Token);
    }
}
