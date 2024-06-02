package com.example.demo.Infrastructure.Persistence.Repositories;

import com.example.demo.Domain.Interfaces.IConvocationRequirementRepository;
import com.example.demo.Infrastructure.Persistence.Cruds.IConvocationRequirementCrudRepository;
import com.example.demo.Infrastructure.Persistence.Entities.ConvocationRequirementEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
@Repository
public class ConvocationRequirementRepository implements IConvocationRequirementRepository {
    @Autowired
    private IConvocationRequirementCrudRepository convocationRequirementCrudRepository;

    @Override
    public List<ConvocationRequirementEntity> createConvocationRequirements(List<ConvocationRequirementEntity> convocationRequirements) {
        List<ConvocationRequirementEntity> response = new ArrayList<>();
        convocationRequirements.forEach(convocationRequirement -> {
            ConvocationRequirementEntity convocationCreated = convocationRequirementCrudRepository.save(convocationRequirement);
            response.add(convocationCreated);
        });
        return response;
    }
}
