package com.example.demo.Application.Services;

import com.example.demo.Application.Dtos.StudentDto;
import com.example.demo.Application.Exceptions.NotFoundException;
import com.example.demo.Application.IStudentService;
import com.example.demo.Application.Mappers.StudentMapper;
import com.example.demo.Domain.Interfaces.IStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService implements IStudentService {
    @Autowired
    private IStudentRepository studentRepository;
    @Autowired
    private StudentMapper studentMapper;

    @Override
    public List<StudentDto> getAllStudents() {
        return this.studentRepository.findAll().stream()
                .map(this.studentMapper::toDto)
                .toList();
    }

    @Override
    public StudentDto getStudentById(int studentId) {
        return this.studentRepository.findById(studentId)
                .map(this.studentMapper::toDto)
                .orElseThrow(() -> new NotFoundException("No existe un estudiante con id " + studentId));
    }
}
