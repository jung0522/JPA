package io.goorm.jpa.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long validityInMilliseconds;

    public JwtTokenProvider(
            @Value("${jwt.secret:my-secret-key-for-jwt-token-generation-must-be-at-least-256-bits}") String secret,
            @Value("${jwt.token-validity-in-seconds:86400}") long validityInSeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.validityInMilliseconds = validityInSeconds * 1000;
    }

    public String createToken(String userId, String role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .subject(userId)
                .claim("role", role)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String userId = claims.getSubject();
        String role = claims.get("role", String.class);

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(role.split(","))
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(userId, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        }
        return false;
    }

    public String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
