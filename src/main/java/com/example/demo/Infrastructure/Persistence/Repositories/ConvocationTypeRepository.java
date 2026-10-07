package com.example.demo.Infrastructure.Persistence.Repositories;

import com.example.demo.Domain.Entities.ConvocationType;
import com.example.demo.Domain.Interfaces.IConvocationTypeRepository;
import com.example.demo.Infrastructure.Persistence.Cruds.IConvocationTypeCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ConvocationTypeRepository implements IConvocationTypeRepository {
    @Autowired
    private IConvocationTypeCrudRepository convocationTypeCrudRepository;

    @Override
    public List<ConvocationType> findAll() {
        List<ConvocationType> response = new ArrayList<>();
        this.convocationTypeCrudRepository.findAll().forEach(convocationTypeEntity ->
                response.add(new ConvocationType(convocationTypeEntity.getId(), convocationTypeEntity.getConvocationTypeName())));
        return response;
    }
}
