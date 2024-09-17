package com.example.demo.Application.Dtos;

import org.springframework.http.HttpStatus;

public class CustomResponse<T> {
    private T data;
    private int codeState;
    private String message;

    public CustomResponse(T data, String message) {
        this.data = data;
        this.message = message;
        this.codeState = HttpStatus.OK.value();
    }

    public CustomResponse(T data, int codeState, String message) {
        this.data = data;
        this.codeState = codeState;
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCodeState() {
        return codeState;
    }

    public void setCodeState(int codeState) {
        this.codeState = codeState;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
