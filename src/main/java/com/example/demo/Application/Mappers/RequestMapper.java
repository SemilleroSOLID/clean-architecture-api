package com.example.demo.Application.Mappers;

import com.example.demo.Application.Dtos.RequestDto;
import com.example.demo.Application.Dtos.RequestRequirementDto;
import com.example.demo.Domain.Entities.Request;
import com.example.demo.Domain.Entities.RequestRequirement;
import org.springframework.stereotype.Component;

@Component
public class RequestMapper {

    public Request toDomain(RequestDto requestDto) {
        Request request = new Request();
        request.setStudentId(requestDto.getStudentId());
        request.setConvocationId(requestDto.getConvocationId());
        request.setRequirements(requestDto.getRequirements().stream()
                .map(requirement -> new RequestRequirement(0, 0, requirement.getConvocationRequirementId(),
                        requirement.getValue(), requirement.getAttachment()))
                .toList());
        return request;
    }

    public RequestDto toDto(Request request) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(request.getId());
        requestDto.setStudentId(request.getStudentId());
        requestDto.setConvocationId(request.getConvocationId());
        requestDto.setCreationDate(request.getCreationDate());
        requestDto.setState(request.getState());
        requestDto.setRequirements(request.getRequirements().stream().map(this::toDto).toList());
        return requestDto;
    }

    private RequestRequirementDto toDto(RequestRequirement requestRequirement) {
        RequestRequirementDto requestRequirementDto = new RequestRequirementDto();
        requestRequirementDto.setId(requestRequirement.getId());
        requestRequirementDto.setConvocationRequirementId(requestRequirement.getConvocationRequirementId());
        requestRequirementDto.setValue(requestRequirement.getValue());
        requestRequirementDto.setAttachment(requestRequirement.getAttachment());
        return requestRequirementDto;
    }
}
