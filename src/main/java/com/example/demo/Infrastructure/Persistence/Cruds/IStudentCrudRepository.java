package com.example.demo.Infrastructure.Persistence.Cruds;

import com.example.demo.Infrastructure.Persistence.Entities.StudentEntity;
import org.springframework.data.repository.CrudRepository;

public interface IStudentCrudRepository extends CrudRepository<StudentEntity, Integer> {
}
