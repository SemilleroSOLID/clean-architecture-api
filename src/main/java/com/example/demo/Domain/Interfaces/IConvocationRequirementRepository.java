package com.example.demo.Domain.Interfaces;

import com.example.demo.Domain.Entities.ConvocationRequirement;
import com.example.demo.Infrastructure.Persistence.Entities.ConvocationRequirementEntity;

import java.util.List;

public interface IConvocationRequirementRepository{
    List<ConvocationRequirementEntity> createConvocationRequirements(List<ConvocationRequirementEntity> convocationRequirements);
}
