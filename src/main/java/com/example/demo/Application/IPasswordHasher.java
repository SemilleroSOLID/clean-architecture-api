package com.example.demo.Application;

/** Port to check passwords against their stored hash; the algorithm (BCrypt) is a detail of Infrastructure. */
public interface IPasswordHasher {
    String hash(String rawPassword);

    boolean matches(String rawPassword, String passwordHash);
}
