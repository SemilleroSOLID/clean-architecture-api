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
    // Messages of CU-002: main flow and alternate flow (the student does not meet every requirement).
    static final String REQUEST_ACTIVE_MESSAGE = "Su solicitud ha sido creada exitosamente y cumple con todos los requisitos";
    static final String REQUEST_PENDING_MESSAGE = "Su solicitud ha sido creada y está pendiente de aprobación aunque no cumples con todos los requisitos";

    @Autowired
    private IRequestService requestService;

    @PostMapping("/createRequest")
    public CustomResponse<RequestDto> createRequest(@Valid @RequestBody RequestDto request) {
        RequestDto createdRequest = this.requestService.createRequest(request);
        String message = createdRequest.getUnmetRequirements().isEmpty() ? REQUEST_ACTIVE_MESSAGE : REQUEST_PENDING_MESSAGE;
        return new CustomResponse<>(createdRequest, message);
    }
}
