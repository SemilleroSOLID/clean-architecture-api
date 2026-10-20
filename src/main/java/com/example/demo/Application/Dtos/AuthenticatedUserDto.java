package com.example.demo.Application.Dtos;

import com.example.demo.Domain.Enums.EnumRole;

/**
 * Who is calling, as read from the token (issue #22). The Api layer passes it to the services as plain data;
 * neither Application nor Domain know how the user was authenticated.
 */
public class AuthenticatedUserDto {
    private final int userId;
    private final String email;
    private final EnumRole role;
    private final Integer studentId;

    public AuthenticatedUserDto(int userId, String email, EnumRole role, Integer studentId) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.studentId = studentId;
    }

    public boolean isAdmin() {
        return role == EnumRole.ADMIN;
    }

    public int getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public EnumRole getRole() {
        return role;
    }

    public Integer getStudentId() {
        return studentId;
    }
}
