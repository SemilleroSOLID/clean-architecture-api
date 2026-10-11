package com.example.demo.Application;

import com.example.demo.Application.Dtos.RequestDto;

public interface IRequestService {
    RequestDto createRequest(RequestDto request);
}
