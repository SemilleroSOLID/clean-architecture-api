package com.example.demo.Application.Mappers;

import com.example.demo.Domain.Entities.ConvocationType;
import com.example.demo.Infrastructure.Persistence.Entities.ConvocationTypeEntity;

public interface IConvocationTypeMapper {
    ConvocationType convocationTypeEntityToConvocationType(final ConvocationTypeEntity convocationTypeEntity);
    ConvocationTypeEntity convocationTypeToConvocationTypeEntity(final ConvocationType convocationType);
}
