package com.example.demo.Application.Services;

import com.example.demo.Application.Dtos.ConvocationDto;
import com.example.demo.Application.Dtos.ConvocationTypeDto;
import com.example.demo.Application.Exceptions.NotFoundException;
import com.example.demo.Application.IConvocationService;
import com.example.demo.Application.Mappers.IConvocationMapper;
import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Entities.ConvocationRequirement;
import com.example.demo.Domain.Entities.Requirement;
import com.example.demo.Domain.Enums.EnumConvocationState;
import com.example.demo.Domain.Exceptions.DomainValidationException;
import com.example.demo.Domain.Interfaces.IConvocationRepository;
import com.example.demo.Domain.Interfaces.IConvocationRequirementRepository;
import com.example.demo.Domain.Interfaces.IConvocationTypeRepository;
import com.example.demo.Domain.Interfaces.IRequirementRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ConvocationService implements IConvocationService {
    @Autowired
    private IConvocationTypeRepository convocationTypeRepository;
    @Autowired
    private IConvocationRepository convocationRepository;
    @Autowired
    private IConvocationRequirementRepository convocationRequirementRepository;
    @Autowired
    private IRequirementRepository requirementRepository;
    @Autowired
    private IConvocationMapper convocationMapper;
    @Autowired
    private Clock clock;

    @Override
    public List<ConvocationDto> getAllConvocation(){
        return this.convocationRepository.findAll().stream()
                .map(this::withRequirements)
                .map(this.convocationMapper::toDto)
                .toList();
    }

    @Override
    public ConvocationDto getConvocationById(int convocationId) {
        Convocation convocation = this.convocationRepository.findById(convocationId)
                .orElseThrow(() -> new NotFoundException("No existe una convocatoria con id " + convocationId));
        return this.convocationMapper.toDto(this.withRequirements(convocation));
    }

    @Override
    public List<ConvocationTypeDto> getAllConvocationTypes() {
        return this.convocationTypeRepository.findAll().stream()
                .map(this.convocationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ConvocationDto createConvocation(ConvocationDto convocationDto) {
        Convocation convocation = this.convocationMapper.toDomain(convocationDto);
        convocation.validate(LocalDate.now(this.clock), this.getRequirementCatalog());
        convocation.setState(EnumConvocationState.OPEN);

        Convocation createdConvocation = this.convocationRepository.save(convocation);

        List<ConvocationRequirement> convocationRequirements = convocation.getConvocationRequirements();
        convocationRequirements.forEach(convocationRequirement -> convocationRequirement.setConvocationId(createdConvocation.getId()));
        createdConvocation.setConvocationRequirements(this.convocationRequirementRepository.saveAll(convocationRequirements));

        return this.convocationMapper.toDto(createdConvocation);
    }

    @Override
    @Transactional
    public ConvocationDto updateConvocation(int convocationId, ConvocationDto convocationDto) {
        Convocation currentConvocation = this.convocationRepository.findById(convocationId)
                .orElseThrow(() -> new NotFoundException("No existe una convocatoria con id " + convocationId));

        Convocation convocation = this.convocationMapper.toDomain(convocationDto);
        convocation.setId(convocationId);
        convocation.setState(currentConvocation.getState());
        convocation.validateChanges(currentConvocation.getStartDate(), LocalDate.now(this.clock), this.getRequirementCatalog());

        Set<Integer> currentRequirementIds = this.convocationRequirementRepository.findConvocationRequirements(convocationId).stream()
                .map(ConvocationRequirement::getId)
                .collect(Collectors.toSet());
        List<ConvocationRequirement> convocationRequirements = convocation.getConvocationRequirements();
        Map<String, String> errors = new LinkedHashMap<>();
        for (int index = 0; index < convocationRequirements.size(); index++) {
            int requirementId = convocationRequirements.get(index).getId();
            if (requirementId != 0 && !currentRequirementIds.contains(requirementId)) {
                errors.put("convocationRequirements[" + index + "].id",
                        "El requisito " + requirementId + " no pertenece a esta convocatoria");
            }
        }
        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }

        Convocation updatedConvocation = this.convocationRepository.save(convocation);

        Set<Integer> keptRequirementIds = convocationRequirements.stream()
                .map(ConvocationRequirement::getId)
                .filter(id -> id != 0)
                .collect(Collectors.toSet());
        this.convocationRequirementRepository.deleteByIds(currentRequirementIds.stream()
                .filter(id -> !keptRequirementIds.contains(id))
                .toList());
        convocationRequirements.forEach(convocationRequirement -> convocationRequirement.setConvocationId(convocationId));
        updatedConvocation.setConvocationRequirements(this.convocationRequirementRepository.saveAll(convocationRequirements));

        return this.convocationMapper.toDto(updatedConvocation);
    }

    private Map<Integer, Requirement> getRequirementCatalog() {
        return this.requirementRepository.findAll().stream()
                .collect(Collectors.toMap(Requirement::getId, Function.identity()));
    }

    private Convocation withRequirements(Convocation convocation) {
        convocation.setConvocationRequirements(this.convocationRequirementRepository.findConvocationRequirements(convocation.getId()));
        return convocation;
    }
}
