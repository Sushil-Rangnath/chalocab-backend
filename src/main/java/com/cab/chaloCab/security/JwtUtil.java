package com.cab.chaloCab.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    @Value("${jwt.refreshExpirationMs}")
    private long refreshExpirationMs;

    private Key signingKey;

    @PostConstruct
    public void init() {
        // ensure secret length is adequate for HS256/HS512; Keys.hmacShaKeyFor will throw if too short
        signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ------------------------
    // Token generation (overloads)
    // ------------------------

    /**
     * Generate access token with a default role "USER".
     * Useful for call sites that only pass username.
     */
    public String generateToken(String username) {
        return generateToken(username, "USER");
    }

    /**
     * Generate access token with explicit role.
     * The role is stored in the token as a cleaned value (without ROLE_ prefix),
     * e.g. "ADMIN" instead of "ROLE_ADMIN".
     */
    public String generateToken(String username, String role) {
        String cleanedRole = cleanRole(role);
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(username)
                .claim("role", cleanedRole)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtExpirationMs))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generate refresh token with a default role "USER".
     */
    public String generateRefreshToken(String username) {
        return generateRefreshToken(username, "USER");
    }

    /**
     * Generate refresh token with explicit role.
     */
    public String generateRefreshToken(String username, String role) {
        String cleanedRole = cleanRole(role);
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(username)
                .claim("role", cleanedRole)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + refreshExpirationMs))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // ------------------------
    // Validation helpers
    // ------------------------

    /**
     * Validate token signature and expiry (no user check).
     */
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token); // will throw if invalid
            return !isTokenExpired(token);
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Validate token against provided UserDetails (username match + not expired).
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        if (token == null || userDetails == null) return false;
        final String username = extractUsername(token);
        return username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Validate token against username (string).
     */
    public boolean validateToken(String token, String username) {
        if (token == null || username == null) return false;
        final String extracted = extractUsername(token);
        return extracted != null && extracted.equals(username) && !isTokenExpired(token);
    }

    // ------------------------
    // Extraction helpers
    // ------------------------

    /**
     * Extract username (subject) from token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Alias: extractEmail for older call sites that used extractEmail(...)
     */
    public String extractEmail(String token) {
        return extractUsername(token);
    }

    /**
     * Extract role claim. Could be "ADMIN" or "USER" (no ROLE_ prefix).
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ------------------------
    // Internal helpers
    // ------------------------

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Public method to check expiry state.
     */
    public boolean isTokenExpired(String token) {
        try {
            Date exp = extractExpiration(token);
            return exp.before(new Date());
        } catch (Exception e) {
            // treat parsing problems as expired/invalid
            return true;
        }
    }

    /**
     * Normalize role string: remove ROLE_ prefix if present.
     */
    private String cleanRole(String role) {
        if (role == null) return "USER";
        role = role.trim();
        if (role.startsWith("ROLE_")) {
            return role.substring(5);
        }
        return role;
    }
}
