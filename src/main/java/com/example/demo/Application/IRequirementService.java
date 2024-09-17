package com.example.demo.Application;

import com.example.demo.Application.Dtos.ConditionRequirementDto;
import com.example.demo.Application.Dtos.RequirementDto;

import java.util.List;

public interface IRequirementService {
    List<RequirementDto> getAllRequirements();
    List<ConditionRequirementDto> getAllConditionsRequirement();
}
