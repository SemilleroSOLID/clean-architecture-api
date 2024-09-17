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
        ConvocationType convocationType = this.modelMapper.map(convocationTypeEntity, ConvocationType.class);
        convocationType.setId(convocationType.getId()-1);
        return convocationType;
    }

    @Override
    public ConvocationTypeEntity convocationTypeToConvocationTypeEntity(ConvocationType convocationType) {
        return this.modelMapper.map(convocationType, ConvocationTypeEntity.class);
    }
}
