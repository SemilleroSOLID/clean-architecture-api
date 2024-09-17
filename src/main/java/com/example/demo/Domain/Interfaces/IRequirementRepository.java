package com.example.demo.Domain.Interfaces;

import com.example.demo.Application.Dtos.RequirementDto;

import java.util.List;

public interface IRequirementRepository {
    List<RequirementDto> getAllRequirements();
}
