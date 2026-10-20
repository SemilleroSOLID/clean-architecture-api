package com.example.demo.Application.Dtos;

import jakarta.validation.constraints.NotBlank;

/** Body of POST /auth/login. */
public class LoginDto {
    @NotBlank(message = "El correo es obligatorio")
    private String email;
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    public LoginDto() {}

    public LoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
