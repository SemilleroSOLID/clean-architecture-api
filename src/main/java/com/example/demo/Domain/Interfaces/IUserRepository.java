package com.example.demo.Domain.Interfaces;

import com.example.demo.Domain.Entities.User;

import java.util.Optional;

public interface IUserRepository {
    Optional<User> findByEmail(String email);
}
