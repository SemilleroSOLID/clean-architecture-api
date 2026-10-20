package com.example.demo.Domain.Entities;

import com.example.demo.Domain.Enums.EnumRole;

/**
 * Someone who can sign in (issue #22). A student user is linked to its {@link Student}; the administrator is not.
 * The password is only kept as a hash.
 */
public class User {
    private int id;
    private String email;
    private String passwordHash;
    private EnumRole role;
    private Integer studentId;

    public User() {}

    public User(int id, String email, String passwordHash, EnumRole role, Integer studentId) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.studentId = studentId;
    }

    public boolean isStudent() {
        return role == EnumRole.STUDENT;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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
