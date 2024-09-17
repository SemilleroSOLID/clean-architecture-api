package com.example.demo.Infrastructure.Persistence.Cruds;

import com.example.demo.Infrastructure.Persistence.Entities.RequirementEntity;
import org.springframework.data.repository.CrudRepository;

public interface IRequirementCrudRepository extends CrudRepository<RequirementEntity, Integer> {

}
