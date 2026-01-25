package com.epam.gym_crm.workload.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;

@Service
public class IntegrationJwtService {
    private final SecretKey key;

    public IntegrationJwtService(@Value("${integration.jwt.secret}") String secretBase64
    ) {
        byte[] bytes = Base64.getDecoder().decode(secretBase64);
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public String extractSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            var claimsJws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            String subject = claimsJws.getBody().getSubject();
            return "gym-crm".equals(subject);

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
