package com.example.demo.Infrastructure.Persistence.Cruds;

import com.example.demo.Infrastructure.Persistence.Entities.RequestEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface IRequestCrudRepository extends CrudRepository<RequestEntity, Integer> {
    boolean existsByConvocationId(Integer convocationId);
    boolean existsByConvocationIdAndRequestStateIn(Integer convocationId, Collection<Integer> requestStates);
}
