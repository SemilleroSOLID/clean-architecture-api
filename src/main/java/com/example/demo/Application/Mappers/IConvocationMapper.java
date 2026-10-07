package com.example.demo.Application.Mappers;

import com.example.demo.Application.Dtos.ConvocationDto;
import com.example.demo.Application.Dtos.ConvocationRequirementDto;
import com.example.demo.Application.Dtos.ConvocationTypeDto;
import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Entities.ConvocationRequirement;
import com.example.demo.Domain.Entities.ConvocationType;

public interface IConvocationMapper {
    ConvocationDto toDto(Convocation convocation);
    Convocation toDomain(ConvocationDto convocationDto);
    ConvocationRequirementDto toDto(ConvocationRequirement convocationRequirement);
    ConvocationRequirement toDomain(ConvocationRequirementDto convocationRequirementDto);
    ConvocationTypeDto toDto(ConvocationType convocationType);
}
