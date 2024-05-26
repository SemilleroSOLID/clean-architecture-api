package com.example.demo.Infrastructure.Mappers;

import com.example.demo.Domain.Entities.ConvocationType;
import com.example.demo.Application.Mappers.IConvocationTypeMapper;
import com.example.demo.Infrastructure.Persistence.Entities.ConvocationTypeEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ConvocationTypeMapper implements IConvocationTypeMapper {
    private final ModelMapper modelMapper;
    public ConvocationTypeMapper(){
        this.modelMapper = new ModelMapper();
    }
    @Override
    public ConvocationType convocationTypeEntityToConvocationType(final ConvocationTypeEntity convocationTypeEntity){
        return this.modelMapper.map(convocationTypeEntity, ConvocationType.class);
    }

    @Override
    public ConvocationTypeEntity convocationTypeToConvocationTypeEntity(ConvocationType convocationType) {
        return this.modelMapper.map(convocationType, ConvocationTypeEntity.class);
    }
}
