package com.example.demo.Infrastructure.Persistence.Repositories;

import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Interfaces.IConvocationRepository;
import com.example.demo.Infrastructure.Mappers.ConvocationPersistenceMapper;
import com.example.demo.Infrastructure.Persistence.Cruds.IConvocationCrudRepository;
import com.example.demo.Infrastructure.Persistence.Entities.ConvocationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ConvocationRepository implements IConvocationRepository {
    @Autowired
    private IConvocationCrudRepository convocationCrudRepository;
    @Autowired
    private ConvocationPersistenceMapper convocationPersistenceMapper;

    @Override
    public Convocation save(Convocation convocation) {
        ConvocationEntity savedEntity = this.convocationCrudRepository.save(this.convocationPersistenceMapper.toEntity(convocation));
        return this.convocationPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public List<Convocation> findAll() {
        List<Convocation> response = new ArrayList<>();
        this.convocationCrudRepository.findAll()
                .forEach(convocationEntity -> response.add(this.convocationPersistenceMapper.toDomain(convocationEntity)));
        return response;
    }

    @Override
    public Optional<Convocation> findById(int convocationId) {
        return this.convocationCrudRepository.findById(convocationId).map(this.convocationPersistenceMapper::toDomain);
    }
}
