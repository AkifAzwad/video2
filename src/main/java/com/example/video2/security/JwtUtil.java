package com.example.video2.security;

import com.example.video2.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

@Log4j2
@Component
public class JwtUtil {
    public static final String BEARER = "Bearer ";
    public static final int BEARER_LENGTH = BEARER.length();
    public static final int JWT_TOKEN_VALIDITY_MS = 10 * 60 * 60 * 1000; // 10 Hours

    private final SecretKey secretKey;


    public JwtUtil(@Value("${security.jwt.secret}") final String secret) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }


    public String generateToken(final User user) {
        return Jwts.builder()
                .claims(Map.of(JwtClaims.ROLE, user.getRole()))
                .subject(user.getUsername())
                .issuedAt(new Date(currentTimeMillis()))
                .expiration(new Date(currentTimeMillis() + JWT_TOKEN_VALIDITY_MS))
                .signWith(secretKey)
                .compact();
    }


    public Optional<JwtClaims> parse(final HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (isNull(authHeader) || !authHeader.startsWith(BEARER))
            return empty();

        final String jwtToken = authHeader.substring(BEARER_LENGTH);
        if ("null".equals(jwtToken))
            return empty();

        return ofNullable(verify(jwtToken));
    }

    public JwtClaims verify(final String token) {
        try {
            return JwtClaims.with(
                    Jwts.parser()
                            .verifyWith(secretKey)
                            .build()
                            .parseSignedClaims(token)
                            .getPayload()
            );
        } catch (final ExpiredJwtException e) {
            log.error("JWT expired [{}]: {}", token, e.getMessage());
            return null;
        } catch (final SignatureException e) {
            log.error("Invalid signature [{}]: {}", token, e.getMessage());
            throw e;
        } catch (final MalformedJwtException e) {
            log.error("Malformed JWT [{}]: {}", token, e.getMessage());
            return null;
        } catch (final NoSuchElementException e) {
            log.error("Invalid JWT [{}]: {}", token, e.getMessage());
            return null;
        } catch (final Exception e) {
            log.error("Failed to parse JWT [{}]: {}", token, e.getMessage());
            throw e;
        }
    }
}
