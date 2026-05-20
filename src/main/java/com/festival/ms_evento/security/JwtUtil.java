package com.festival.ms_evento.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    private static final String SECRET =
            "festival-secret-key-2024-muy-larga-para-cumplir-256bits";

    // GENERAR CLAVE
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // EXTRAER EMAIL
    public String extraerEmail(String token) {
        return getClaims(token).getSubject();
    }

    // EXTRAER ROL
    public String extraerRol(String token) {
        return getClaims(token).get("rol", String.class);
    }

    // VALIDAR TOKEN
    public boolean esValido(String token) {

        try {

            getClaims(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }

    // OBTENER CLAIMS
    private Claims getClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}