package com.example.demo.Infrastructure.Persistence.Cruds;

import com.example.demo.Domain.Entities.Convocation;
import org.springframework.data.repository.CrudRepository;

public interface IConvocationCrudRepository extends CrudRepository<Convocation, Integer> {
}
