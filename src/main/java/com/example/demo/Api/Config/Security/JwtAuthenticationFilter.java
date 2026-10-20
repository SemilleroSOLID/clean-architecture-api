package com.example.demo.Api.Config.Security;

import com.example.demo.Application.Dtos.AuthenticatedUserDto;
import com.example.demo.Application.ITokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/** Reads "Authorization: Bearer <token>" and, if the token is valid, sets the user of the request. */
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    static final String INVALID_TOKEN = "invalidToken";
    private static final String BEARER = "Bearer ";

    private final ITokenProvider tokenProvider;

    public JwtAuthenticationFilter(ITokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(BEARER)) {
            Optional<AuthenticatedUserDto> user = this.tokenProvider.read(header.substring(BEARER.length()));
            if (user.isPresent()) {
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.get().getRole().name()));
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(user.get(), null, authorities));
            } else {
                // A protected endpoint will answer 401 telling the session expired, not that it is missing.
                request.setAttribute(INVALID_TOKEN, true);
            }
        }
        chain.doFilter(request, response);
    }
}
