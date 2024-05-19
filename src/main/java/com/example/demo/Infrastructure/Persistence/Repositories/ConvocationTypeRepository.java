package com.example.demo.Infrastructure.Persistence.Repositories;

import com.example.demo.Domain.Entities.ConvocationType;
import com.example.demo.Domain.Interfaces.IConvocationTypeRepository;
import com.example.demo.Infrastructure.Persistence.Cruds.IConvocationTypeCrudRepository;
import com.example.demo.Infrastructure.Persistence.Entities.ConvocationTypeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ConvocationTypeRepository implements IConvocationTypeRepository {
    @Autowired
    private IConvocationTypeCrudRepository convocationTypeCrudRepository;
    @Override
    public List<ConvocationType> getAll() {
        Iterable<ConvocationTypeEntity> convocationTypes = convocationTypeCrudRepository.findAll();
        List<ConvocationType> response = new ArrayList<>();
        convocationTypes.forEach(convocation -> response.add(new ConvocationType(convocation.getId(), convocation.getConvocationTypeName())));
        return response;
    }
}
