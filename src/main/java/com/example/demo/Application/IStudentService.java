package com.example.demo.Application;

import com.example.demo.Application.Dtos.StudentDto;

import java.util.List;

public interface IStudentService {
    List<StudentDto> getAllStudents();
    StudentDto getStudentById(int studentId);
}
