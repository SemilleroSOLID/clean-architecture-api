package com.example.demo.Application.Mappers;

import com.example.demo.Application.Dtos.RequestDetailDto;
import com.example.demo.Application.Dtos.RequestDto;
import com.example.demo.Application.Dtos.RequestRequirementDto;
import com.example.demo.Application.Dtos.RequestSummaryDto;
import com.example.demo.Application.Dtos.RequirementCheckDto;
import com.example.demo.Application.Dtos.StudentDto;
import com.example.demo.Application.Dtos.StudentRequestDto;
import com.example.demo.Application.Dtos.UnmetRequirementDto;
import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Entities.ConvocationRequirement;
import com.example.demo.Domain.Entities.Request;
import com.example.demo.Domain.Entities.RequestRequirement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        return unmetRequirements.stream()
                .map(requirement -> new UnmetRequirementDto(requirement.getId(), requirement.getName(),
                        requirement.getConditional(), requirement.getRequiredValue(), request.valueOf(requirement.getId())))
                .toList();
    }

    public RequestSummaryDto toSummaryDto(Request request, String studentName, String convocationTitle,
                                          List<ConvocationRequirement> unmetRequirements) {
        RequestSummaryDto summary = new RequestSummaryDto();
        summary.setId(request.getId());
        summary.setStudentId(request.getStudentId());
        summary.setStudentName(studentName);
        summary.setConvocationId(request.getConvocationId());
        summary.setConvocationTitle(convocationTitle);
        summary.setCreationDate(request.getCreationDate());
        summary.setState(request.getState());
        summary.setUnmetRequirements(unmetRequirements.stream().map(ConvocationRequirement::getName).toList());
        return summary;
    }

    /** The request with, for each requirement of the convocation, the value of the student and whether it is met. */
    public RequestDetailDto toDetailDto(Request request, StudentDto student, Convocation convocation,
                                        List<ConvocationRequirement> convocationRequirements,
                                        List<ConvocationRequirement> unmetRequirements) {
        Set<Integer> unmetIds = unmetRequirements.stream().map(ConvocationRequirement::getId).collect(Collectors.toSet());
        RequestDetailDto detail = new RequestDetailDto();
        detail.setId(request.getId());
        detail.setCreationDate(request.getCreationDate());
        detail.setState(request.getState());
        detail.setComment(request.getComment());
        detail.setStudent(student);
        detail.setConvocationId(convocation.getId());
        detail.setConvocationTitle(convocation.getTitle());
        detail.setConvocationType(convocation.getType());
        detail.setConvocationState(convocation.getState());
        detail.setRequirements(convocationRequirements.stream()
                .map(requirement -> {
                    RequirementCheckDto check = new RequirementCheckDto();
                    check.setConvocationRequirementId(requirement.getId());
                    check.setName(requirement.getName());
                    check.setConditional(requirement.getConditional());
                    check.setRequiredValue(requirement.getRequiredValue());
                    request.answerTo(requirement.getId()).ifPresent(answer -> {
                        check.setValue(answer.getValue());
                        check.setAttachment(answer.getAttachment());
                    });
                    check.setMet(!unmetIds.contains(requirement.getId()));
                    return check;
                })
                .toList());
        return detail;
    }

    public StudentRequestDto toStudentRequestDto(Request request, Convocation convocation) {
        StudentRequestDto studentRequest = new StudentRequestDto();
        studentRequest.setId(request.getId());
        studentRequest.setCreationDate(request.getCreationDate());
        studentRequest.setState(request.getState());
        studentRequest.setComment(request.getComment());
        studentRequest.setConvocationId(convocation.getId());
        studentRequest.setConvocationTitle(convocation.getTitle());
        studentRequest.setConvocationType(convocation.getType());
        studentRequest.setConvocationStartDate(convocation.getStartDate());
        studentRequest.setConvocationEndDate(convocation.getEndDate());
        studentRequest.setConvocationState(convocation.getState());
        return studentRequest;
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
