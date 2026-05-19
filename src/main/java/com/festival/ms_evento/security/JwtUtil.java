package com.festival.ms_evento.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;


@Component
public class JwtUtil {

    private final String SECRET = "festival-secret-key-2024-muy-larga-para-cumplir-256bits";

    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String extraerEmail(String token) {
        return getClaims(token).getSubject();
    }

    public String extraerRol(String token) {
        return getClaims(token).get("rol", String.class);
    }

    public boolean esValido(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private Claims getClaims(String token) {
    return Jwts.parser()
            .verifyWith((javax.crypto.SecretKey) getKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}