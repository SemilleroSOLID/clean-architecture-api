package com.example.demo.Domain.Interfaces;

import com.example.demo.Domain.Entities.Student;

import java.util.List;
import java.util.Optional;

public interface IStudentRepository {
    List<Student> findAll();
    Optional<Student> findById(int studentId);
    boolean existsById(int studentId);
}
