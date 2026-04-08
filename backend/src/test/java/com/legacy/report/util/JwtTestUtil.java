package com.legacy.report.util;

import com.legacy.report.security.JwtTokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * Utility class for generating test JWT tokens.
 */
@Component
public class JwtTestUtil {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration}")
    private long validityInMs;

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTestUtil(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Generate a valid JWT token for a username using the main provider.
     */
    public String generateToken(String username) {
        return jwtTokenProvider.createToken(username);
    }

    /**
     * Generate an expired JWT token for testing expired token scenarios.
     */
    public String generateExpiredToken(String username) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        // Token expired 1 hour ago
        Date validity = new Date(now.getTime() - 3600000);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(now.getTime() - 7200000))
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generate a token with an invalid signature.
     */
    public String generateInvalidToken(String username) {
        Key key = Keys.hmacShaKeyFor("wrong-secret-key-for-testing".getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generate a malformed token.
     */
    public String generateMalformedToken() {
        return "invalid.token.here";
    }

    /**
     * Create authorization header value with Bearer prefix.
     */
    public String createAuthorizationHeader(String token) {
        return "Bearer " + token;
    }
}
