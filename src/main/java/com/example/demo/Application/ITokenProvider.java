package com.example.demo.Application;

import com.example.demo.Application.Dtos.AuthenticatedUserDto;

import java.time.Instant;
import java.util.Optional;

/** Port to issue and read access tokens; the format (JWT) is a detail of Infrastructure. */
public interface ITokenProvider {
    IssuedToken issue(AuthenticatedUserDto user);

    /** Empty when the token is malformed, tampered with or expired. */
    Optional<AuthenticatedUserDto> read(String token);

    record IssuedToken(String value, Instant expiresAt) {}
}
