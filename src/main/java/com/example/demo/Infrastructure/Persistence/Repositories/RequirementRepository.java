package com.example.demo.Infrastructure.Persistence.Repositories;

import com.example.demo.Domain.Entities.Requirement;
import com.example.demo.Domain.Interfaces.IRequirementRepository;
import com.example.demo.Infrastructure.Persistence.Cruds.IRequirementCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RequirementRepository implements IRequirementRepository {
    @Autowired
    private IRequirementCrudRepository requirementCrudRepository;

    @Override
    public List<Requirement> findAll() {
        List<Requirement> requirements = new ArrayList<>();
        this.requirementCrudRepository.findAll().forEach(requirementEntity ->
                requirements.add(new Requirement(requirementEntity.getId(), requirementEntity.getRequirementName(),
                        Boolean.TRUE.equals(requirementEntity.getGrade()))));
        return requirements;
    }
}
