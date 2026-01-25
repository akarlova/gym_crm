package com.epam.gym_crm.integration.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
public class IntegrationJwtService {
    private final SecretKey key;
    private final long expirationMinutes;

    public IntegrationJwtService(
            @Value("${integration.jwt.secret}") String secretBase64,
            @Value("${integration.jwt.expiration-minutes:120}") long expirationMinutes
    ) {
        byte[] bytes = Base64.getDecoder().decode(secretBase64);
        this.key = Keys.hmacShaKeyFor(bytes);
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(String subject) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationMinutes * 60);
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
