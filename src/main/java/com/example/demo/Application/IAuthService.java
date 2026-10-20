package com.example.demo.Application;

import com.example.demo.Application.Dtos.AuthenticatedUserDto;
import com.example.demo.Application.Dtos.LoginDto;
import com.example.demo.Application.Dtos.SessionDto;

public interface IAuthService {
    SessionDto login(LoginDto login);

    SessionDto me(AuthenticatedUserDto user);
}
