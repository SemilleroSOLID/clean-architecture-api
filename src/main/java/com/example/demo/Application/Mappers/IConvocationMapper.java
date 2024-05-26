package com.example.demo.Application.Mappers;

import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Infrastructure.Persistence.Entities.ConvocationEntity;

public interface IConvocationMapper {
    Convocation convocationEntityToConvocation(final ConvocationEntity convocationEntity);
    ConvocationEntity convocationToConvocationEntity(final Convocation convocation);
}
