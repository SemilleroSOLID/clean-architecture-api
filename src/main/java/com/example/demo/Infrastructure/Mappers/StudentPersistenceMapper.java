package com.example.demo.Infrastructure.Mappers;

import com.example.demo.Domain.Entities.Student;
import com.example.demo.Infrastructure.Persistence.Entities.StudentEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class StudentPersistenceMapper {

    public Student toDomain(StudentEntity studentEntity) {
        return new Student(
                studentEntity.getId(),
                studentEntity.getNameStudent(),
                studentEntity.getEmail(),
                studentEntity.getProgram(),
                studentEntity.getAverage(),
                studentEntity.getStratum(),
                new ArrayList<>());
    }
}
