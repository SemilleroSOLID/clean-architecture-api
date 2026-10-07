package com.example.demo.Api.Config;

import com.example.demo.Application.Dtos.CustomResponse;
import com.example.demo.Application.Exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CustomResponse<Object>> handleNotFound(NotFoundException exception) {
        CustomResponse<Object> response = new CustomResponse<>(null, HttpStatus.NOT_FOUND.value(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
