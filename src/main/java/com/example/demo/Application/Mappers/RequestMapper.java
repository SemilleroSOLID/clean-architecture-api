package com.example.demo.Application.Mappers;

import com.example.demo.Application.Dtos.RequestDto;
import com.example.demo.Application.Dtos.RequestRequirementDto;
import com.example.demo.Application.Dtos.UnmetRequirementDto;
import com.example.demo.Domain.Entities.ConvocationRequirement;
import com.example.demo.Domain.Entities.Request;
import com.example.demo.Domain.Entities.RequestRequirement;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /** Unmet requirements with the value the student gave for each one. */
    public List<UnmetRequirementDto> toUnmetDtos(List<ConvocationRequirement> unmetRequirements, Request request) {
        Map<Integer, String> valueByRequirement = new HashMap<>();
        request.getRequirements().forEach(answer -> valueByRequirement.put(answer.getConvocationRequirementId(), answer.getValue()));
        return unmetRequirements.stream()
                .map(requirement -> new UnmetRequirementDto(requirement.getId(), requirement.getName(),
                        requirement.getConditional(), requirement.getRequiredValue(), valueByRequirement.get(requirement.getId())))
                .toList();
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
