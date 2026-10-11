package com.example.demo.Api.Controllers;

import com.example.demo.Api.Dtos.CustomResponse;
import com.example.demo.Application.Dtos.RequestDto;
import com.example.demo.Application.IRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("request")
public class RequestController {

    @Autowired
    private IRequestService requestService;

    @PostMapping("/createRequest")
    public CustomResponse<RequestDto> createRequest(@Valid @RequestBody RequestDto request) {
        return new CustomResponse<>(this.requestService.createRequest(request), "Solicitud creada");
    }
}
