package com.example.demo.Infrastructure.Mappers;

import com.example.demo.Application.Mappers.IConvocationMapper;
import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Infrastructure.Persistence.Entities.ConvocationEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ConvocationMapper implements IConvocationMapper {
    private final ModelMapper modelMapper;
    public ConvocationMapper(){
        this.modelMapper = new ModelMapper();
    }

    @Override
    public Convocation convocationEntityToConvocation(ConvocationEntity convocationEntity) {
        return this.modelMapper.map(convocationEntity,  Convocation.class);
    }

    @Override
    public ConvocationEntity convocationToConvocationEntity(Convocation convocation) {
        return this.modelMapper.map(convocation,  ConvocationEntity.class);
    }
}
