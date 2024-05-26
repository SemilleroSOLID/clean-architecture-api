package com.example.demo.Infrastructure.Persistence.Repositories;

import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Interfaces.IConvocationRepository;
import com.example.demo.Infrastructure.Persistence.Cruds.IConvocationCrudRepository;
import com.example.demo.Infrastructure.Persistence.Cruds.IConvocationTypeCrudRepository;
import com.example.demo.Infrastructure.Persistence.Entities.ConvocationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ConvocationRepository implements IConvocationRepository {
    @Autowired
    private IConvocationCrudRepository convocationCrudRepository;

    @Override
    public List<Convocation> getAllConvocation() {
        return null;
    }

    @Override
    public ConvocationEntity createConvocation(ConvocationEntity convocationEntity) {
        ConvocationEntity convocation = this.convocationCrudRepository.save(convocationEntity);
        return convocation;
    }
}
