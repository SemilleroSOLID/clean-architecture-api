package com.example.demo.Infrastructure.Mappers;

import com.example.demo.Application.Dtos.ConvocationDto;
import com.example.demo.Application.Dtos.ConvocationRequirementDto;
import com.example.demo.Application.Mappers.IConvocationMapper;
import com.example.demo.Domain.Enums.EnumConditionRequirement;
import com.example.demo.Domain.Enums.EnumConvocationState;
import com.example.demo.Domain.Enums.EnumConvocationType;
import com.example.demo.Infrastructure.Persistence.Entities.ConvocationEntity;
import com.example.demo.Infrastructure.Persistence.Entities.ConvocationRequirementEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ConvocationMapper implements IConvocationMapper {
    private final ModelMapper modelMapper;
    public ConvocationMapper(){
        this.modelMapper = new ModelMapper();
    }

    @Override
    public ConvocationDto convocationEntityToConvocationDto(ConvocationEntity convocationEntity) {
        ConvocationDto convocation = this.modelMapper.map(convocationEntity, ConvocationDto.class);
        convocation.setState(EnumConvocationState.fromValue(convocationEntity.getStateConvocationId()));
        convocation.setType(EnumConvocationType.fromValue(convocationEntity.getConvocationTypeId()));
        return convocation;
    }

    @Override
    public ConvocationEntity convocationDtoToConvocationEntity(ConvocationDto convocation) {
        ConvocationEntity convocationEntity = this.modelMapper.map(convocation, ConvocationEntity.class);
        convocationEntity.setConvocationTypeId(convocation.getType().getValue());
        convocationEntity.setStateConvocationId(convocation.getState().getValue());
        return convocationEntity;
    }

    @Override
    public List<ConvocationRequirementEntity> listConvocationRequirementDtoToListConvocationRequirementEntity(List<ConvocationRequirementDto> convocationRequirementDtos) {
        List<ConvocationRequirementEntity> response = new ArrayList<>();
        convocationRequirementDtos.forEach(convocationRequirementDto -> {
            ConvocationRequirementEntity convocationRequirement = this.modelMapper.map(convocationRequirementDto, ConvocationRequirementEntity.class);
            convocationRequirement.setConditionalId(convocationRequirementDto.getConditional().getValue());
            convocationRequirement.setRequirementId(convocationRequirementDto.getRequirementId());
            response.add(convocationRequirement);
        });
        return response;
    }

    @Override
    public List<ConvocationRequirementDto> listConvocationRequirementEntityToListConvocationRequirementDto
            (List<ConvocationRequirementEntity> convocationRequirementEntities) {

        List<ConvocationRequirementDto> response = new ArrayList<>();
        convocationRequirementEntities.forEach(convocationRequirementEntity -> {
            ConvocationRequirementDto convocationRequirement = this.modelMapper.map(convocationRequirementEntity, ConvocationRequirementDto.class);
            convocationRequirement.setConditional(EnumConditionRequirement.fromValue(convocationRequirementEntity.getConditionalId()));
            convocationRequirement.setRequirementId(convocationRequirementEntity.getRequirementId());
            response.add(convocationRequirement);
        });
        return response;
    }
}
