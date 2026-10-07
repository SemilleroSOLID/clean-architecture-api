package com.example.demo.Api.Config;

import com.example.demo.Api.Dtos.CustomResponse;
import com.example.demo.Application.Exceptions.NotFoundException;
import com.example.demo.Domain.Exceptions.DomainValidationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CustomResponse<Object>> handleNotFound(NotFoundException exception) {
        CustomResponse<Object> response = new CustomResponse<>(null, HttpStatus.NOT_FOUND.value(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponse<Map<String, String>>> handleInvalidArguments(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(fieldError -> errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage()));
        return badRequest(errors);
    }

    @ExceptionHandler(DomainValidationException.class)
    public ResponseEntity<CustomResponse<Map<String, String>>> handleDomainValidation(DomainValidationException exception) {
        return badRequest(exception.getErrors());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomResponse<Map<String, String>>> handleUnreadableBody(HttpMessageNotReadableException exception) {
        if (exception.getCause() instanceof JsonMappingException mappingException && !mappingException.getPath().isEmpty()) {
            String field = mappingException.getPath().stream()
                    .map(reference -> reference.getFieldName() != null ? reference.getFieldName() : "[" + reference.getIndex() + "]")
                    .collect(Collectors.joining(".")).replace(".[", "[");
            return badRequest(Map.of(field, "El valor del campo " + field + " no es válido"));
        }
        CustomResponse<Map<String, String>> response =
                new CustomResponse<>(Map.of(), HttpStatus.BAD_REQUEST.value(), "El cuerpo de la petición no es un JSON válido");
        return ResponseEntity.badRequest().body(response);
    }

    private ResponseEntity<CustomResponse<Map<String, String>>> badRequest(Map<String, String> errors) {
        String message = String.join("; ", errors.values());
        return ResponseEntity.badRequest().body(new CustomResponse<>(errors, HttpStatus.BAD_REQUEST.value(), message));
    }
}
