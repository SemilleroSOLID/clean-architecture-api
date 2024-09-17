package com.example.demo.Infrastructure.Persistence.Cruds;

import com.example.demo.Infrastructure.Persistence.Entities.ConvocationTypeEntity;
import org.springframework.data.repository.CrudRepository;

public interface IConvocationTypeCrudRepository extends CrudRepository<ConvocationTypeEntity, Integer> {
}
