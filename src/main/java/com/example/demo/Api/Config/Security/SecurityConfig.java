package com.example.demo.Api.Config.Security;

import com.example.demo.Application.ITokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Who can call each endpoint (issue #22). Stateless: every request brings its JWT in the Authorization header,
 * so there is no session and no CSRF cookie to protect. CORS keeps the rules of WebConfig.
 */
@Configuration
public class SecurityConfig {
    static final String ADMIN = "ADMIN";
    static final String STUDENT = "STUDENT";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ITokenProvider tokenProvider,
                                                   SecurityErrorWriter errorWriter) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/login", "/test/hello", "/error").permitAll()
                        // Administrator: manages convocations, students and every request
                        .requestMatchers("/convocation/createConvocation", "/convocation/updateConvocation/**",
                                "/convocation/deleteConvocation/**", "/convocation/closeConvocation/**",
                                "/convocation/reopenConvocation/**", "/convocation/finishConvocation/**",
                                "/convocation/cancelConvocation/**").hasRole(ADMIN)
                        .requestMatchers("/student/**", "/request/getAllRequests",
                                "/request/approveRequest/**", "/request/rejectRequest/**").hasRole(ADMIN)
                        // Student: applies, withdraws and sees his own requests
                        .requestMatchers("/request/createRequest", "/request/cancelRequest/**",
                                "/request/getMyRequests").hasRole(STUDENT)
                        // Both: convocations, requirements, /auth/me and the detail or history of a request
                        // (the service checks that a student only sees his own)
                        .anyRequest().authenticated())
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((request, response, exception) -> errorWriter.write(response,
                                HttpStatus.UNAUTHORIZED, request.getAttribute(JwtAuthenticationFilter.INVALID_TOKEN) != null
                                        ? "La sesión no es válida o venció: inicie sesión de nuevo"
                                        : "Debe iniciar sesión para usar este recurso"))
                        .accessDeniedHandler((request, response, exception) -> errorWriter.write(response,
                                HttpStatus.FORBIDDEN, "No tiene permiso para usar este recurso")))
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
