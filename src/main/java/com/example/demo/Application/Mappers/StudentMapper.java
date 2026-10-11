package com.example.demo.Application.Mappers;

import com.example.demo.Application.Dtos.StudentDto;
import com.example.demo.Domain.Entities.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public StudentDto toDto(Student student) {
        return new StudentDto(student.getId(), student.getName(), student.getEmail(), student.getProgram(),
                student.getAverage(), student.getStratum());
    }
}
