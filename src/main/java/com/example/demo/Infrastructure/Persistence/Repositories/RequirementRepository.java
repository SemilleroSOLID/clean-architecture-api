package com.example.demo.Infrastructure.Persistence.Repositories;

import com.example.demo.Application.Dtos.RequirementDto;
import com.example.demo.Domain.Interfaces.IRequirementRepository;
import com.example.demo.Infrastructure.Persistence.Cruds.IRequirementCrudRepository;
import com.example.demo.Infrastructure.Persistence.Entities.RequirementEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RequirementRepository implements IRequirementRepository {
    @Autowired
    private IRequirementCrudRepository requirementCrudRepository;

    @Override
    public List<RequirementDto> getAllRequirements() {
        List<RequirementDto> requirementDtos = new ArrayList<>();
        Iterable<RequirementEntity> requirementEntities = this.requirementCrudRepository.findAll();
        requirementEntities.forEach(requirementEntity ->
        {requirementDtos.add(new RequirementDto(requirementEntity.getId(),
                requirementEntity.getRequirementName()));
        });
        return requirementDtos;
    }
}
