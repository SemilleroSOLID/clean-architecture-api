package com.example.demo.Infrastructure.Persistence.Cruds;

import com.example.demo.Infrastructure.Persistence.Entities.ConvocationEntity;
import org.springframework.data.repository.CrudRepository;

public interface IConvocationCrudRepository extends CrudRepository<ConvocationEntity, Integer> {
}
