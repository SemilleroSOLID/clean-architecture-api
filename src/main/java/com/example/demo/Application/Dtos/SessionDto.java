package com.example.demo.Application.Dtos;

import com.example.demo.Domain.Enums.EnumRole;

import java.time.Instant;

/** The signed in user: answer of POST /auth/login (with the token) and of GET /auth/me (without it). */
public class SessionDto {
    private String token;
    private Instant expiresAt;
    private String email;
    private String name;
    private EnumRole role;
    private Integer studentId;

    public SessionDto() {}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EnumRole getRole() {
        return role;
    }

    public void setRole(EnumRole role) {
        this.role = role;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }
}
