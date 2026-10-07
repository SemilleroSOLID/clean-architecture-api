package com.example.demo.Domain.Interfaces;

import com.example.demo.Domain.Entities.ConvocationRequirement;

import java.util.Collection;
import java.util.List;

public interface IConvocationRequirementRepository {
    List<ConvocationRequirement> saveAll(List<ConvocationRequirement> convocationRequirements);
    List<ConvocationRequirement> findConvocationRequirements(int convocationId);
    void deleteByIds(Collection<Integer> convocationRequirementIds);
}
