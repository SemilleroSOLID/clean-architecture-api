package com.example.demo.Api.Controllers;

import com.example.demo.Api.Dtos.CustomResponse;
import com.example.demo.Application.Dtos.StudentDto;
import com.example.demo.Application.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("student")
public class StudentController {
    @Autowired
    private IStudentService studentService;

    @GetMapping("/getAllStudents")
    public CustomResponse<List<StudentDto>> getAllStudents() {
        return new CustomResponse<>(this.studentService.getAllStudents(), "Lista de estudiantes");
    }

    @GetMapping("/getStudentById/{studentId}")
    public CustomResponse<StudentDto> getStudentById(@PathVariable("studentId") Integer studentId) {
        StudentDto student = this.studentService.getStudentById(studentId);
        return new CustomResponse<>(student, "Estudiante con id " + student.getId());
    }
}
