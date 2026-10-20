package com.example.demo.Infrastructure.Security;

import com.example.demo.Application.Dtos.AuthenticatedUserDto;
import com.example.demo.Application.ITokenProvider;
import com.example.demo.Domain.Enums.EnumRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/** Signed JWT (HS256) with the id, email, role and student of the user (issue #22). */
@Component
public class JwtTokenProvider implements ITokenProvider {
    private static final String EMAIL = "email";
    private static final String ROLE = "role";
    private static final String STUDENT_ID = "studentId";

    private final SecretKey key;
    private final Duration validity;
    private final Clock clock;

    public JwtTokenProvider(@Value("${app.jwt.secret}") String secret,
                            @Value("${app.jwt.expiration-minutes:480}") long expirationMinutes,
                            Clock clock) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.validity = Duration.ofMinutes(expirationMinutes);
        this.clock = clock;
    }

    @Override
    public IssuedToken issue(AuthenticatedUserDto user) {
        Instant now = this.clock.instant();
        Instant expiresAt = now.plus(this.validity);
        String token = Jwts.builder()
                .subject(String.valueOf(user.getUserId()))
                .claim(EMAIL, user.getEmail())
                .claim(ROLE, user.getRole().name())
                .claim(STUDENT_ID, user.getStudentId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(this.key)
                .compact();
        return new IssuedToken(token, expiresAt);
    }

    @Override
    public Optional<AuthenticatedUserDto> read(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(this.key)
                    .clock(() -> Date.from(this.clock.instant()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(new AuthenticatedUserDto(Integer.parseInt(claims.getSubject()), claims.get(EMAIL, String.class),
                    EnumRole.valueOf(claims.get(ROLE, String.class)), claims.get(STUDENT_ID, Integer.class)));
        } catch (JwtException | IllegalArgumentException exception) {
            // Malformed, tampered with, signed with another key or expired
            return Optional.empty();
        }
    }
}
