package com.example.demo.Api.Controllers;

import com.example.demo.Api.Dtos.CustomResponse;
import com.example.demo.Application.Dtos.AuthenticatedUserDto;
import com.example.demo.Application.Dtos.RejectRequestDto;
import com.example.demo.Application.Dtos.RequestDetailDto;
import com.example.demo.Application.Dtos.RequestDto;
import com.example.demo.Application.Dtos.RequestSummaryDto;
import com.example.demo.Application.Dtos.StudentRequestDto;
import com.example.demo.Application.IRequestService;
import com.example.demo.Domain.Enums.EnumStateRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("request")
public class RequestController {
    // Messages of CU-002: main flow and alternate flow (the student does not meet every requirement).
    static final String REQUEST_ACTIVE_MESSAGE = "Su solicitud ha sido creada exitosamente y cumple con todos los requisitos";
    static final String REQUEST_PENDING_MESSAGE = "Su solicitud ha sido creada y está pendiente de aprobación aunque no cumples con todos los requisitos";

    @Autowired
    private IRequestService requestService;

    // Who can call each endpoint is in Api/Config/SecurityConfig (issue #22); the student always comes from the token.
    @PostMapping("/createRequest")
    public CustomResponse<RequestDto> createRequest(@Valid @RequestBody RequestDto request,
                                                    @AuthenticationPrincipal AuthenticatedUserDto user) {
        RequestDto createdRequest = this.requestService.createRequest(request, user.getStudentId());
        String message = createdRequest.getUnmetRequirements().isEmpty() ? REQUEST_ACTIVE_MESSAGE : REQUEST_PENDING_MESSAGE;
        return new CustomResponse<>(createdRequest, message);
    }

    @GetMapping("/getAllRequests")
    public CustomResponse<List<RequestSummaryDto>> getAllRequests(@RequestParam(required = false) Integer convocationId,
                                                                  @RequestParam(required = false) EnumStateRequest state) {
        return new CustomResponse<>(this.requestService.getAllRequests(convocationId, state), "Lista de solicitudes");
    }

    @GetMapping("/getRequestById/{requestId}")
    public CustomResponse<RequestDetailDto> getRequestById(@PathVariable("requestId") Integer requestId,
                                                           @AuthenticationPrincipal AuthenticatedUserDto user) {
        RequestDetailDto request = this.requestService.getRequestById(requestId, user);
        return new CustomResponse<>(request, "Solicitud con id " + request.getId());
    }

    @PutMapping("/approveRequest/{requestId}")
    public CustomResponse<RequestDetailDto> approveRequest(@PathVariable("requestId") Integer requestId) {
        return new CustomResponse<>(this.requestService.approveRequest(requestId), "Solicitud aprobada");
    }

    @PutMapping("/rejectRequest/{requestId}")
    public CustomResponse<RequestDetailDto> rejectRequest(@PathVariable("requestId") Integer requestId,
                                                          @Valid @RequestBody RejectRequestDto reject) {
        return new CustomResponse<>(this.requestService.rejectRequest(requestId, reject.getComment()), "Solicitud rechazada");
    }

    @GetMapping("/getRequestsByStudent/{studentId}")
    public CustomResponse<List<StudentRequestDto>> getRequestsByStudent(@PathVariable("studentId") Integer studentId,
                                                                        @AuthenticationPrincipal AuthenticatedUserDto user) {
        return new CustomResponse<>(this.requestService.getRequestsByStudent(studentId, user), "Solicitudes del estudiante");
    }

    @GetMapping("/getMyRequests")
    public CustomResponse<List<StudentRequestDto>> getMyRequests(@AuthenticationPrincipal AuthenticatedUserDto user) {
        return new CustomResponse<>(this.requestService.getRequestsByStudent(user.getStudentId(), user), "Mis solicitudes");
    }

    @PutMapping("/cancelRequest/{requestId}")
    public CustomResponse<StudentRequestDto> cancelRequest(@PathVariable("requestId") Integer requestId,
                                                           @AuthenticationPrincipal AuthenticatedUserDto user) {
        return new CustomResponse<>(this.requestService.cancelRequest(requestId, user.getStudentId()), "Solicitud retirada");
    }
}
