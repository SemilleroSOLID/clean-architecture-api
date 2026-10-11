package com.example.demo.Infrastructure.Persistence.Repositories;

import com.example.demo.Domain.Entities.Student;
import com.example.demo.Domain.Interfaces.IStudentRepository;
import com.example.demo.Infrastructure.Mappers.StudentPersistenceMapper;
import com.example.demo.Infrastructure.Persistence.Cruds.IStudentCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class StudentRepository implements IStudentRepository {
    @Autowired
    private IStudentCrudRepository studentCrudRepository;
    @Autowired
    private StudentPersistenceMapper studentPersistenceMapper;

    @Override
    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        this.studentCrudRepository.findAll().forEach(studentEntity -> students.add(this.studentPersistenceMapper.toDomain(studentEntity)));
        return students;
    }

    @Override
    public Optional<Student> findById(int studentId) {
        return this.studentCrudRepository.findById(studentId).map(this.studentPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsById(int studentId) {
        return this.studentCrudRepository.existsById(studentId);
    }
}
