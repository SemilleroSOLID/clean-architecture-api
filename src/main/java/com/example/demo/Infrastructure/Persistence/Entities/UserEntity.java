package com.example.demo.Infrastructure.Persistence.Entities;

import com.example.demo.Domain.Enums.EnumRole;
import jakarta.persistence.*;

// "User" is a reserved word in SQL Server, so the table is Users (issue #22).
@Entity
@Table(name = "Users", schema = "dbo")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Unique in the table (see create-database.sql)
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    // BCrypt hash, never the password
    @Column(name = "passwordHash", nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private EnumRole role;

    // Only for students
    @Column(name = "studentId")
    private Integer studentId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
