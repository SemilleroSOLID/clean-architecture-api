package com.example.demo.Infrastructure.Persistence.Cruds;

import com.example.demo.Infrastructure.Persistence.Entities.ConvocationRequirementEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IConvocationRequirementCrudRepository extends CrudRepository<ConvocationRequirementEntity, Integer> {
    List<ConvocationRequirementEntity> findByConvocationId(Integer convocationId);
}
