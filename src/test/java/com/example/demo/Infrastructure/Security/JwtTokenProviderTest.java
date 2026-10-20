package com.example.demo.Infrastructure.Security;

import com.example.demo.Application.Dtos.AuthenticatedUserDto;
import com.example.demo.Application.ITokenProvider;
import com.example.demo.Domain.Enums.EnumRole;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenProviderTest {
    private static final String SECRET = "VBbZjciznAmZtR8z1yLkPDatgsAW0752x18csvABysU=";
    private static final String OTHER_SECRET = "c2VjcmV0by1kZS1vdHJvLXNlcnZpZG9yLTMyLWJ5dGVz";
    private static final Instant NOW = Instant.parse("2026-10-20T05:00:00Z");
    private static final AuthenticatedUserDto LAURA = new AuthenticatedUserDto(2, "laura.gomez@ucaldas.test", EnumRole.STUDENT, 1);

    private static JwtTokenProvider provider(String secret, Instant now) {
        return new JwtTokenProvider(secret, 480, Clock.fixed(now, ZoneOffset.UTC));
    }

    @Test
    void theTokenCarriesTheUserAndExpiresInEightHours() {
        ITokenProvider.IssuedToken token = provider(SECRET, NOW).issue(LAURA);
        assertEquals(NOW.plus(Duration.ofHours(8)), token.expiresAt());

        AuthenticatedUserDto read = provider(SECRET, NOW).read(token.value()).orElseThrow();
        assertEquals(2, read.getUserId());
        assertEquals("laura.gomez@ucaldas.test", read.getEmail());
        assertEquals(EnumRole.STUDENT, read.getRole());
        assertEquals(1, read.getStudentId());
    }

    @Test
    void theAdministratorTokenHasNoStudent() {
        AuthenticatedUserDto admin = new AuthenticatedUserDto(1, "admin@ucaldas.test", EnumRole.ADMIN, null);
        AuthenticatedUserDto read = provider(SECRET, NOW).read(provider(SECRET, NOW).issue(admin).value()).orElseThrow();
        assertTrue(read.isAdmin());
        assertEquals(null, read.getStudentId());
    }

    @Test
    void anExpiredTokenIsRejected() {
        String token = provider(SECRET, NOW).issue(LAURA).value();
        assertFalse(provider(SECRET, NOW.plus(Duration.ofHours(8)).plusSeconds(1)).read(token).isPresent());
    }

    @Test
    void aTamperedTokenOrOneSignedWithAnotherKeyIsRejected() {
        String token = provider(SECRET, NOW).issue(LAURA).value();
        // Change the role in the payload without signing it again
        String[] parts = token.split("\\.");
        String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1])).replace("STUDENT", "ADMIN");
        String tampered = parts[0] + "." + java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes()) + "." + parts[2];
        assertNotEquals(token, tampered);

        assertEquals(Optional.empty(), provider(SECRET, NOW).read(tampered));
        assertEquals(Optional.empty(), provider(OTHER_SECRET, NOW).read(token));
        assertEquals(Optional.empty(), provider(SECRET, NOW).read("no-es-un-token"));
    }
}
