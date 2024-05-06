package com.example.demo.Infrastructure.Persistence.Repositories;

import com.example.demo.Domain.Interfaces.IStudentRepository;
import com.example.demo.Infrastructure.Persistence.Cruds.IStudentCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class StudentRepository implements IStudentRepository {
    @Autowired
    private IStudentCrudRepository studentCrudRepository;
}
