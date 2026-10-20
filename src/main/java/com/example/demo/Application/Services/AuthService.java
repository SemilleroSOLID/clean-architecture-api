package com.example.demo.Application.Services;

import com.example.demo.Application.Dtos.AuthenticatedUserDto;
import com.example.demo.Application.Dtos.LoginDto;
import com.example.demo.Application.Dtos.SessionDto;
import com.example.demo.Application.Exceptions.UnauthorizedException;
import com.example.demo.Application.IAuthService;
import com.example.demo.Application.IPasswordHasher;
import com.example.demo.Application.ITokenProvider;
import com.example.demo.Domain.Entities.Student;
import com.example.demo.Domain.Entities.User;
import com.example.demo.Domain.Interfaces.IStudentRepository;
import com.example.demo.Domain.Interfaces.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService implements IAuthService {
    // The same answer for an unknown email and a wrong password, so the login does not reveal who has an account.
    static final String INVALID_CREDENTIALS = "Correo o contraseña incorrectos";
    static final String ADMIN_NAME = "Administrador";

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IStudentRepository studentRepository;
    @Autowired
    private IPasswordHasher passwordHasher;
    @Autowired
    private ITokenProvider tokenProvider;

    private String dummyHash;

    public AuthService() {}

    AuthService(IUserRepository userRepository, IStudentRepository studentRepository,
                IPasswordHasher passwordHasher, ITokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.passwordHasher = passwordHasher;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public SessionDto login(LoginDto login) {
        Optional<User> found = this.userRepository.findByEmail(login.getEmail().trim());
        // An unknown email also checks a hash, so it takes as long as a wrong password.
        String hash = found.map(User::getPasswordHash).orElseGet(this::dummyHash);
        boolean matches = this.passwordHasher.matches(login.getPassword(), hash);
        if (found.isEmpty() || !matches) {
            throw new UnauthorizedException(INVALID_CREDENTIALS);
        }
        User user = found.get();
        AuthenticatedUserDto authenticated = new AuthenticatedUserDto(user.getId(), user.getEmail(), user.getRole(), user.getStudentId());
        ITokenProvider.IssuedToken token = this.tokenProvider.issue(authenticated);
        SessionDto session = this.me(authenticated);
        session.setToken(token.value());
        session.setExpiresAt(token.expiresAt());
        return session;
    }

    @Override
    public SessionDto me(AuthenticatedUserDto user) {
        SessionDto session = new SessionDto();
        session.setEmail(user.getEmail());
        session.setRole(user.getRole());
        session.setStudentId(user.getStudentId());
        session.setName(user.getStudentId() == null ? ADMIN_NAME
                : this.studentRepository.findById(user.getStudentId()).map(Student::getName).orElse(user.getEmail()));
        return session;
    }

    private synchronized String dummyHash() {
        if (this.dummyHash == null) {
            this.dummyHash = this.passwordHasher.hash("not-a-real-password");
        }
        return this.dummyHash;
    }
}
