package com.example.demo.Infrastructure.Persistence.Repositories;

import com.example.demo.Domain.Entities.ConvocationRequirement;
import com.example.demo.Domain.Interfaces.IConvocationRequirementRepository;
import com.example.demo.Infrastructure.Mappers.ConvocationPersistenceMapper;
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
    @Autowired
    private ConvocationPersistenceMapper convocationPersistenceMapper;

    @Override
    public List<ConvocationRequirement> saveAll(List<ConvocationRequirement> convocationRequirements) {
        List<ConvocationRequirementEntity> entities = convocationRequirements.stream()
                .map(this.convocationPersistenceMapper::toEntity)
                .toList();
        List<ConvocationRequirement> response = new ArrayList<>();
        this.convocationRequirementCrudRepository.saveAll(entities)
                .forEach(savedEntity -> response.add(this.convocationPersistenceMapper.toDomain(savedEntity)));
        return response;
    }

    @Override
    public List<ConvocationRequirement> findConvocationRequirements(int convocationId) {
        return this.convocationRequirementCrudRepository.findByConvocationId(convocationId).stream()
                .map(this.convocationPersistenceMapper::toDomain)
                .toList();
    }
}
