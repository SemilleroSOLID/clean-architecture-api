package com.example.demo.Infrastructure.Persistence.Cruds;

import com.example.demo.Infrastructure.Persistence.Entities.RequestEntity;
import org.springframework.data.repository.CrudRepository;

public interface IRequestCrudRepository extends CrudRepository<RequestEntity, Integer> {
    boolean existsByConvocationId(Integer convocationId);
}
