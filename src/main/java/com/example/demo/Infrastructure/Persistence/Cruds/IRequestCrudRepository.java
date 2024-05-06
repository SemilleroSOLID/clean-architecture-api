package com.example.demo.Infrastructure.Persistence.Cruds;

import com.example.demo.Domain.Entities.Request;
import org.springframework.data.repository.CrudRepository;

public interface IRequestCrudRepository extends CrudRepository<Request, Integer> {
}
