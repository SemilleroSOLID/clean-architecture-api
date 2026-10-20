package com.example.demo.Infrastructure.Security;

import com.example.demo.Application.IPasswordHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/** Passwords are stored as BCrypt hashes (issue #22): slow on purpose and with a salt of their own. */
@Component
public class BCryptPasswordHasher implements IPasswordHasher {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String hash(String rawPassword) {
        return this.encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String passwordHash) {
        return this.encoder.matches(rawPassword, passwordHash);
    }
}
