package com.example.video2.security;

import com.example.video2.model.Role;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.Objects;

@RequiredArgsConstructor(staticName = "with")
public class JwtClaims {
    public static final String ROLE = "role";

    private final Claims claims;

    public String username() {
        return claims.getSubject();
    }

    public boolean isAdmin() {
        return Objects.equals(Role.ADMIN.name(), claims.get(ROLE, String.class));
    }

    public boolean isValid(final String actualUsername) {
        return Objects.equals(actualUsername, username()) && isNotExpired();
    }

    private boolean isNotExpired() {
        final var now = new Date();
        return now.before(claims.getExpiration());
    }
}
