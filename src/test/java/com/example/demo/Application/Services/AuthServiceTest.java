package com.example.demo.Application.Services;

import com.example.demo.Application.Dtos.AuthenticatedUserDto;
import com.example.demo.Application.Dtos.LoginDto;
import com.example.demo.Application.Dtos.SessionDto;
import com.example.demo.Application.Exceptions.UnauthorizedException;
import com.example.demo.Application.IPasswordHasher;
import com.example.demo.Application.ITokenProvider;
import com.example.demo.Domain.Entities.Student;
import com.example.demo.Domain.Entities.User;
import com.example.demo.Domain.Enums.EnumRole;
import com.example.demo.Domain.Interfaces.IStudentRepository;
import com.example.demo.Domain.Interfaces.IUserRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {
    private static final String HASH = "$2b$10$hash-de-laura";

    private final IUserRepository users = mock(IUserRepository.class);
    private final IStudentRepository students = mock(IStudentRepository.class);
    private final IPasswordHasher hasher = mock(IPasswordHasher.class);
    private final ITokenProvider tokens = mock(ITokenProvider.class);
    private final AuthService authService = new AuthService(users, students, hasher, tokens);

    private void lauraExists() {
        when(users.findByEmail("laura.gomez@ucaldas.test"))
                .thenReturn(Optional.of(new User(2, "laura.gomez@ucaldas.test", HASH, EnumRole.STUDENT, 1)));
        when(students.findById(1)).thenReturn(Optional.of(new Student(1, "Laura Gómez Ríos", "laura.gomez@ucaldas.test",
                "Ingeniería de Sistemas", 4.6, 2, List.of())));
    }

    @Test
    void withTheRightPasswordItGivesATokenWithTheRoleAndTheStudent() {
        lauraExists();
        when(hasher.matches("Estudiante2026*", HASH)).thenReturn(true);
        when(tokens.issue(any())).thenReturn(new ITokenProvider.IssuedToken("jwt", Instant.parse("2026-10-20T13:00:00Z")));

        SessionDto session = authService.login(new LoginDto(" laura.gomez@ucaldas.test ", "Estudiante2026*"));

        assertEquals("jwt", session.getToken());
        assertEquals(EnumRole.STUDENT, session.getRole());
        assertEquals(1, session.getStudentId());
        assertEquals("Laura Gómez Ríos", session.getName());
    }

    @Test
    void aWrongPasswordAndAnUnknownEmailGiveTheSameAnswer() {
        lauraExists();
        when(hasher.matches(eq("otra"), anyString())).thenReturn(false);
        when(hasher.hash(anyString())).thenReturn("$2b$10$dummy");

        String wrongPassword = assertThrows(UnauthorizedException.class,
                () -> authService.login(new LoginDto("laura.gomez@ucaldas.test", "otra"))).getMessage();
        String unknownEmail = assertThrows(UnauthorizedException.class,
                () -> authService.login(new LoginDto("nadie@ucaldas.test", "otra"))).getMessage();

        assertEquals("Correo o contraseña incorrectos", wrongPassword);
        assertEquals(wrongPassword, unknownEmail);
        // The unknown email also checks a hash, so it cannot be told apart by the time it takes
        verify(hasher).matches("otra", "$2b$10$dummy");
        verify(tokens, never()).issue(any());
    }

    @Test
    void theAdministratorHasNoStudent() {
        SessionDto session = authService.me(new AuthenticatedUserDto(1, "admin@ucaldas.test", EnumRole.ADMIN, null));
        assertEquals("Administrador", session.getName());
        assertEquals(null, session.getStudentId());
    }
}
