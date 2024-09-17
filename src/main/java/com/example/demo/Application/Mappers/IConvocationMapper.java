package com.example.demo.Application.Mappers;

import com.example.demo.Application.Dtos.ConvocationDto;
import com.example.demo.Application.Dtos.ConvocationRequirementDto;
import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Infrastructure.Persistence.Entities.ConvocationEntity;
import com.example.demo.Infrastructure.Persistence.Entities.ConvocationRequirementEntity;

import java.util.List;

public interface IConvocationMapper {
    ConvocationDto convocationEntityToConvocationDto(final ConvocationEntity convocationEntity);
    ConvocationEntity convocationDtoToConvocationEntity(final ConvocationDto convocation);
    List<ConvocationRequirementEntity> listConvocationRequirementDtoToListConvocationRequirementEntity(List<ConvocationRequirementDto> convocationRequirementDtos);
    List<ConvocationRequirementDto> listConvocationRequirementEntityToListConvocationRequirementDto(List<ConvocationRequirementEntity> convocationRequirementEntities);
}
