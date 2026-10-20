package com.example.demo.Application.Services;

import com.example.demo.Application.Dtos.StudentDto;
import com.example.demo.Application.Exceptions.NotFoundException;
import com.example.demo.Application.Mappers.StudentMapper;
import com.example.demo.Domain.Entities.Student;
import com.example.demo.Domain.Interfaces.IStudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Reference test of a service (issue #24): the model to follow when testing the other services (#32, #33, #34).
 * Guide in the README, section "Cómo escribir una prueba de servicio".
 *
 * A service test checks the service alone: no Spring, no database. What the service gets from outside
 * (here the student repository) is replaced by a Mockito mock that answers what each test needs.
 */
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    // A fake repository: it returns nothing until a test says what to return with when(...).thenReturn(...).
    @Mock
    private IStudentRepository studentRepository;

    // The mapper is a plain class without dependencies, so the real one is used: mock only what touches the outside.
    @Spy
    private StudentMapper studentMapper = new StudentMapper();

    // The class under test: Mockito creates it and puts the mock and the spy in its @Autowired fields.
    @InjectMocks
    private StudentService studentService;

    private static Student laura() {
        return new Student(1, "Laura Gómez Ríos", "laura.gomez@ucaldas.test", "Ingeniería de Sistemas", 4.6, 2, List.of());
    }

    // Names say what is checked: <situation> + <expected result>.
    @Test
    void anExistingStudentIsReturnedWithAllItsData() {
        // Arrange: the repository finds Laura
        when(studentRepository.findById(1)).thenReturn(Optional.of(laura()));

        // Act
        StudentDto student = studentService.getStudentById(1);

        // Assert: every field of the student reaches the DTO
        assertEquals(1, student.getId());
        assertEquals("Laura Gómez Ríos", student.getName());
        assertEquals("laura.gomez@ucaldas.test", student.getEmail());
        assertEquals("Ingeniería de Sistemas", student.getProgram());
        assertEquals(4.6, student.getAverage());
        assertEquals(2, student.getStratum());
    }

    @Test
    void aStudentThatDoesNotExistIsANotFoundError() {
        // Arrange: the repository finds nobody with id 99
        when(studentRepository.findById(99)).thenReturn(Optional.empty());

        // Act and assert: the service throws NotFoundException (the api answers it with 404)...
        NotFoundException error = assertThrows(NotFoundException.class, () -> studentService.getStudentById(99));

        // ...and its message tells which student is missing
        assertEquals("No existe un estudiante con id 99", error.getMessage());
    }

    @Test
    void theListHasOneStudentPerStudentOfTheRepository() {
        // Arrange
        Student andres = new Student(2, "Andrés Felipe Torres", "andres.torres@ucaldas.test", "Ingeniería de Sistemas", 3.8, 3, List.of());
        when(studentRepository.findAll()).thenReturn(List.of(laura(), andres));

        // Act
        List<StudentDto> students = studentService.getAllStudents();

        // Assert: the result, and that the service asked the repository for the students
        assertEquals(List.of("Laura Gómez Ríos", "Andrés Felipe Torres"), students.stream().map(StudentDto::getName).toList());
        verify(studentRepository).findAll();
    }
}
