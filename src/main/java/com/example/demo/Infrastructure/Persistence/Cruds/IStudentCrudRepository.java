package com.example.demo.Infrastructure.Persistence.Cruds;

import com.example.demo.Domain.Entities.Student;
import org.springframework.data.repository.CrudRepository;

public interface IStudentCrudRepository extends CrudRepository<Student, Integer> {
}
