package com.example.demo.Api.Controllers;

import com.example.demo.Api.Dtos.CustomResponse;
import com.example.demo.Application.Dtos.AuthenticatedUserDto;
import com.example.demo.Application.Dtos.LoginDto;
import com.example.demo.Application.Dtos.SessionDto;
import com.example.demo.Application.IAuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    private IAuthService authService;

    @PostMapping("/login")
    public CustomResponse<SessionDto> login(@Valid @RequestBody LoginDto login) {
        return new CustomResponse<>(this.authService.login(login), "Sesión iniciada");
    }

    @GetMapping("/me")
    public CustomResponse<SessionDto> me(@AuthenticationPrincipal AuthenticatedUserDto user) {
        return new CustomResponse<>(this.authService.me(user), "Usuario de la sesión");
    }
}
