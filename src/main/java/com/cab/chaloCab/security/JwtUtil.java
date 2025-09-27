package com.cab.chaloCab.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;
import java.util.regex.Pattern;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationMs}")
    private long expirationMs;

    @Value("${jwt.refreshExpirationMs}")
    private long refreshExpirationMs;

    private Key signingKey;

    @PostConstruct
    void init() {
        // Support "base64:XXXX" secrets as well as plain text
        byte[] keyBytes;
        if (secret != null && secret.startsWith("base64:")) {
            keyBytes = Base64.getDecoder().decode(secret.substring("base64:".length()));
        } else {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        if (keyBytes.length < 32) {
            throw new IllegalStateException("jwt.secret must be at least 32 bytes for HS256. Current=" + keyBytes.length);
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    private Key getSigningKey() {
        return signingKey;
    }

    // ---------- Token Creation ----------
    public String generateToken(String subject, String role) {
        return buildToken(subject, role, expirationMs);
    }

    public String generateRefreshToken(String subject, String role) {
        return buildToken(subject, role, refreshExpirationMs);
    }

    private String buildToken(String subject, String role, long validityMs) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(subject)
                .claim("role", role)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + validityMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ---------- Validation & Claims ----------
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }

    public boolean validateToken(String token, String expectedSubject) {
        if (!validateToken(token)) return false;
        String actual = getSubjectFromToken(token);
        return expectedSubject != null && expectedSubject.equals(actual);
    }

    public String getSubjectFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String getRoleFromToken(String token) {
        return extractClaim(token, c -> c.get("role", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return resolver.apply(claims);
    }

    // ---------- Compatibility Helpers ----------
    public String extractUsername(String token) { return getSubjectFromToken(token); }
    public String getPhoneFromToken(String token) { return getSubjectFromToken(token); }

    private static final Pattern EMAIL_RX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

    public boolean isEmail(String s) { return s != null && EMAIL_RX.matcher(s).matches(); }
}
