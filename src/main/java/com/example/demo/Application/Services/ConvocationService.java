package com.example.demo.Application.Services;

import com.example.demo.Application.IConvocationService;
import com.example.demo.Application.Mappers.IConvocationMapper;
import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Entities.ConvocationType;
import com.example.demo.Domain.Interfaces.IConvocationRepository;
import com.example.demo.Domain.Interfaces.IConvocationTypeRepository;
import com.example.demo.Infrastructure.Persistence.Entities.ConvocationEntity;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ConvocationService implements IConvocationService {
    @Autowired
    private IConvocationTypeRepository convocationTypeRepository;

    @Autowired
    private IConvocationMapper convocationMapper;

    @Autowired
    private IConvocationRepository convocationRepository;

    @Override
    public List<Convocation> getAllConvocation() {
        return null;
    }

    @Override
    public List<ConvocationType> getAllConvocationTypes() {
        return convocationTypeRepository.getAll();
    }

    @Override
    @Transactional
    public Convocation createConvocation(Convocation convocation){
        ConvocationEntity convocationEntity = this.convocationMapper.convocationToConvocationEntity(convocation);
        convocationEntity.setConvocationTypeId(convocation.getType().getValue());
        convocationEntity.setStateConvocationId(convocation.getState().getValue());
        ConvocationEntity createdConvocationEntity =  this.convocationRepository.createConvocation(convocationEntity);

        return this.convocationMapper.convocationEntityToConvocation(createdConvocationEntity);
    }
}
