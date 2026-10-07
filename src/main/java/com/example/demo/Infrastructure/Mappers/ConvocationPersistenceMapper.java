package com.example.demo.Infrastructure.Mappers;

import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Entities.ConvocationRequirement;
import com.example.demo.Domain.Enums.EnumConditionRequirement;
import com.example.demo.Domain.Enums.EnumConvocationState;
import com.example.demo.Domain.Enums.EnumConvocationType;
import com.example.demo.Infrastructure.Persistence.Entities.ConvocationEntity;
import com.example.demo.Infrastructure.Persistence.Entities.ConvocationRequirementEntity;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.Objects;

@Component
public class ConvocationPersistenceMapper {

    public Convocation toDomain(ConvocationEntity convocationEntity) {
        Convocation convocation = new Convocation();
        convocation.setId(convocationEntity.getId());
        convocation.setTitle(convocationEntity.getTitle());
        convocation.setDescription(convocationEntity.getConvocationDescription());
        convocation.setType(EnumConvocationType.fromValue(convocationEntity.getConvocationTypeId()));
        convocation.setStartDate(convocationEntity.getStartDate().toLocalDate());
        convocation.setEndDate(convocationEntity.getEndDate().toLocalDate());
        convocation.setState(EnumConvocationState.fromValue(convocationEntity.getStateConvocationId()));
        return convocation;
    }

    public ConvocationEntity toEntity(Convocation convocation) {
        ConvocationEntity convocationEntity = new ConvocationEntity();
        if (convocation.getId() != 0) convocationEntity.setId(convocation.getId());
        convocationEntity.setTitle(convocation.getTitle());
        convocationEntity.setConvocationDescription(Objects.requireNonNullElse(convocation.getDescription(), ""));
        convocationEntity.setConvocationTypeId(convocation.getType().getValue());
        convocationEntity.setStartDate(Date.valueOf(convocation.getStartDate()));
        convocationEntity.setEndDate(Date.valueOf(convocation.getEndDate()));
        convocationEntity.setStateConvocationId(convocation.getState().getValue());
        return convocationEntity;
    }

    public ConvocationRequirement toDomain(ConvocationRequirementEntity convocationRequirementEntity) {
        return new ConvocationRequirement(
                convocationRequirementEntity.getId(),
                convocationRequirementEntity.getName(),
                convocationRequirementEntity.getRequiredValue(),
                EnumConditionRequirement.fromValue(convocationRequirementEntity.getConditionalId()),
                convocationRequirementEntity.getRequirementDescription(),
                convocationRequirementEntity.getRequirementId(),
                convocationRequirementEntity.getConvocationId());
    }

    public ConvocationRequirementEntity toEntity(ConvocationRequirement convocationRequirement) {
        ConvocationRequirementEntity convocationRequirementEntity = new ConvocationRequirementEntity();
        if (convocationRequirement.getId() != 0) convocationRequirementEntity.setId(convocationRequirement.getId());
        convocationRequirementEntity.setName(convocationRequirement.getName());
        convocationRequirementEntity.setRequiredValue(convocationRequirement.getRequiredValue());
        convocationRequirementEntity.setConditionalId(convocationRequirement.getConditional().getValue());
        convocationRequirementEntity.setRequirementDescription(Objects.requireNonNullElse(convocationRequirement.getDescription(), ""));
        convocationRequirementEntity.setRequirementId(convocationRequirement.getRequirementId());
        convocationRequirementEntity.setConvocationId(convocationRequirement.getConvocationId());
        return convocationRequirementEntity;
    }
}
