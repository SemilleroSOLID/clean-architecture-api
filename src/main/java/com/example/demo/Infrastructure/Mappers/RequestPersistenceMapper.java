package com.example.demo.Infrastructure.Mappers;

import com.example.demo.Domain.Entities.Request;
import com.example.demo.Domain.Entities.RequestRequirement;
import com.example.demo.Domain.Enums.EnumStateRequest;
import com.example.demo.Infrastructure.Persistence.Entities.RequestEntity;
import com.example.demo.Infrastructure.Persistence.Entities.RequestRequirementEntity;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Component
public class RequestPersistenceMapper {

    public Request toDomain(RequestEntity requestEntity) {
        Request request = new Request();
        request.setId(requestEntity.getId());
        request.setStudentId(requestEntity.getStudentId());
        request.setConvocationId(requestEntity.getConvocationId());
        request.setCreationDate(requestEntity.getCreationDate().toLocalDate());
        request.setState(EnumStateRequest.fromValue(requestEntity.getRequestState()));
        request.setComment(requestEntity.getComment());
        return request;
    }

    public RequestEntity toEntity(Request request) {
        RequestEntity requestEntity = new RequestEntity();
        if (request.getId() != 0) requestEntity.setId(request.getId());
        requestEntity.setStudentId(request.getStudentId());
        requestEntity.setConvocationId(request.getConvocationId());
        requestEntity.setCreationDate(Date.valueOf(request.getCreationDate()));
        requestEntity.setRequestState(request.getState().getValue());
        requestEntity.setComment(request.getComment());
        return requestEntity;
    }

    public RequestRequirement toDomain(RequestRequirementEntity requestRequirementEntity) {
        return new RequestRequirement(
                requestRequirementEntity.getId(),
                requestRequirementEntity.getRequestId(),
                requestRequirementEntity.getConvocationRequirementId(),
                requestRequirementEntity.getRequestValue(),
                requestRequirementEntity.getAttachment());
    }

    public RequestRequirementEntity toEntity(RequestRequirement requestRequirement) {
        RequestRequirementEntity requestRequirementEntity = new RequestRequirementEntity();
        if (requestRequirement.getId() != 0) requestRequirementEntity.setId(requestRequirement.getId());
        requestRequirementEntity.setRequestId(requestRequirement.getRequestId());
        requestRequirementEntity.setConvocationRequirementId(requestRequirement.getConvocationRequirementId());
        requestRequirementEntity.setRequestValue(requestRequirement.getValue().trim());
        requestRequirementEntity.setAttachment(requestRequirement.getAttachment());
        return requestRequirementEntity;
    }
}
