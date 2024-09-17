package com.example.demo.Application.Services;

import com.example.demo.Application.Dtos.ConvocationDto;
import com.example.demo.Application.Dtos.ConvocationRequirementDto;
import com.example.demo.Application.IConvocationService;
import com.example.demo.Application.Mappers.IConvocationMapper;
import com.example.demo.Domain.Entities.ConvocationType;
import com.example.demo.Domain.Enums.EnumConvocationState;
import com.example.demo.Domain.Interfaces.IConvocationRepository;
import com.example.demo.Domain.Interfaces.IConvocationRequirementRepository;
import com.example.demo.Domain.Interfaces.IConvocationTypeRepository;
import com.example.demo.Infrastructure.Persistence.Entities.ConvocationEntity;
import com.example.demo.Infrastructure.Persistence.Entities.ConvocationRequirementEntity;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class ConvocationService implements IConvocationService {
    @Autowired
    private IConvocationTypeRepository convocationTypeRepository;
    @Autowired
    private IConvocationRepository convocationRepository;
    @Autowired
    private IConvocationRequirementRepository convocationRequirementRepository;
    @Autowired
    private IConvocationMapper convocationMapper;

    @Override
    public List<ConvocationDto> getAllConvocation(){
        List<ConvocationDto> convocationsDto = new ArrayList<>();
        List<ConvocationEntity> convocationEntities = this.convocationRepository.getAllConvocations();
        convocationEntities.forEach(convocationEntity -> {
            ConvocationDto convocationDto = this.convocationMapper.convocationEntityToConvocationDto(convocationEntity);
            convocationDto.setConvocationRequirements(new ArrayList<>());
            convocationsDto.add(convocationDto);
        });
        return convocationsDto;
    }

    @Override
    public ConvocationDto getConvocationById(int convocationId) {
        ConvocationEntity convocationEntity = this.convocationRepository.getConvocationById(convocationId);
        ConvocationDto convocationDto = this.convocationMapper.convocationEntityToConvocationDto(convocationEntity);
        convocationDto.setConvocationRequirements(new ArrayList<>());
        return convocationDto;
    }

    @Override
    public List<ConvocationType> getAllConvocationTypes() {
        return this.convocationTypeRepository.getAll();
    }

    @Override
    @Transactional
    public ConvocationDto createConvocation(ConvocationDto convocation) {

        convocation.setState(EnumConvocationState.OPEN);
        ConvocationEntity convocationEntity = this.convocationMapper.convocationDtoToConvocationEntity(convocation);
        ConvocationEntity createdConvocationEntity = this.convocationRepository.createConvocation(convocationEntity);

        convocation.getConvocationRequirements().forEach(convocationRequirement -> convocationRequirement.
                setConvocationId(createdConvocationEntity.getId()));

        List<ConvocationRequirementEntity> convocationRequirementEntities = convocationMapper.
                listConvocationRequirementDtoToListConvocationRequirementEntity(convocation.getConvocationRequirements());

        List<ConvocationRequirementEntity> convocationRequirementEntitiesCreated = this.convocationRequirementRepository
                .createConvocationRequirements(convocationRequirementEntities);

        ConvocationDto convocationRequirementDto = this.convocationMapper.convocationEntityToConvocationDto(createdConvocationEntity);
        convocationRequirementDto.setConvocationRequirements(this.convocationMapper
                .listConvocationRequirementEntityToListConvocationRequirementDto(convocationRequirementEntitiesCreated));

        return convocationRequirementDto;
    }
}
