package com.example.demo.Application.Services;

import com.example.demo.Application.Dtos.ConditionRequirementDto;
import com.example.demo.Application.Dtos.RequirementDto;
import com.example.demo.Application.IRequirementService;
import com.example.demo.Domain.Enums.EnumConditionRequirement;
import com.example.demo.Domain.Interfaces.IRequirementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RequirementService implements IRequirementService {

    @Autowired
    private IRequirementRepository requirementRepository;

    @Override
    public List<RequirementDto> getAllRequirements() {
        return this.requirementRepository.getAllRequirements();
    }

    @Override
    public List<ConditionRequirementDto> getAllConditionsRequirement() {
        List<ConditionRequirementDto> conditionRequirementDtos = new ArrayList<>();
        for (EnumConditionRequirement enumConditionRequirement: EnumConditionRequirement.values()){
            conditionRequirementDtos.add(new ConditionRequirementDto(enumConditionRequirement.getValue(),
                    enumConditionRequirement.name()));
        }
        return conditionRequirementDtos;
    }
}
