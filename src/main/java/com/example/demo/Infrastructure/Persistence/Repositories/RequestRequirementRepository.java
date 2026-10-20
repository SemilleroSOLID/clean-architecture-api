package com.example.demo.Infrastructure.Persistence.Repositories;

import com.example.demo.Domain.Entities.RequestRequirement;
import com.example.demo.Domain.Interfaces.IRequestRequirementRepository;
import com.example.demo.Infrastructure.Mappers.RequestPersistenceMapper;
import com.example.demo.Infrastructure.Persistence.Cruds.IRequestRequirementCrudRepository;
import com.example.demo.Infrastructure.Persistence.Entities.RequestRequirementEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class RequestRequirementRepository implements IRequestRequirementRepository {
    @Autowired
    private IRequestRequirementCrudRepository requestRequirementCrudRepository;
    @Autowired
    private RequestPersistenceMapper requestPersistenceMapper;

    @Override
    public List<RequestRequirement> saveAll(List<RequestRequirement> requestRequirements) {
        List<RequestRequirementEntity> entities = requestRequirements.stream()
                .map(this.requestPersistenceMapper::toEntity)
                .toList();
        List<RequestRequirement> response = new ArrayList<>();
        this.requestRequirementCrudRepository.saveAll(entities)
                .forEach(savedEntity -> response.add(this.requestPersistenceMapper.toDomain(savedEntity)));
        return response;
    }

    @Override
    public List<RequestRequirement> findByRequestId(int requestId) {
        return this.requestRequirementCrudRepository.findByRequestId(requestId).stream()
                .map(this.requestPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<RequestRequirement> findByRequestIds(Collection<Integer> requestIds) {
        if (requestIds.isEmpty()) return List.of();
        return this.requestRequirementCrudRepository.findByRequestIdIn(requestIds).stream()
                .map(this.requestPersistenceMapper::toDomain)
                .toList();
    }
}
