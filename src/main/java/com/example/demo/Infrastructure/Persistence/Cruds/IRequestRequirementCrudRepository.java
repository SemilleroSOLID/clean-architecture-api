package com.example.demo.Infrastructure.Persistence.Cruds;

import com.example.demo.Infrastructure.Persistence.Entities.RequestRequirementEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IRequestRequirementCrudRepository extends CrudRepository<RequestRequirementEntity, Integer> {
    List<RequestRequirementEntity> findByRequestId(Integer requestId);
}
