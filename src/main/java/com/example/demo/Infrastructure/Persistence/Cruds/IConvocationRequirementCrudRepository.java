package com.example.demo.Infrastructure.Persistence.Cruds;

import com.example.demo.Infrastructure.Persistence.Entities.ConvocationRequirementEntity;
import org.springframework.data.repository.CrudRepository;

public interface IConvocationRequirementCrudRepository extends CrudRepository<ConvocationRequirementEntity, Integer> {
}
