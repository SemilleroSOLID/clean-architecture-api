package com.example.demo.Application.Services;

import com.example.demo.Application.Dtos.AuthenticatedUserDto;
import com.example.demo.Application.Exceptions.ForbiddenException;
import com.example.demo.Domain.Entities.Request;
import com.example.demo.Domain.Enums.EnumRole;
import com.example.demo.Domain.Interfaces.IRequestRepository;
import com.example.demo.Domain.Interfaces.IRequestRequirementRepository;
import com.example.demo.Domain.Interfaces.IStudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

// A student cannot see the requests of another student (issue #22).
@ExtendWith(MockitoExtension.class)
class RequestAccessTest {
    private static final AuthenticatedUserDto LAURA = new AuthenticatedUserDto(2, "laura.gomez@ucaldas.test", EnumRole.STUDENT, 1);

    @Mock
    private IRequestRepository requestRepository;
    @Mock
    private IRequestRequirementRepository requestRequirementRepository;
    @Mock
    private IStudentRepository studentRepository;
    @InjectMocks
    private RequestService requestService;

    @Test
    void aStudentCannotSeeTheHistoryOfAnother() {
        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> requestService.getRequestsByStudent(2, LAURA));
        assertEquals("Solo puedes ver tus propias solicitudes", exception.getMessage());
        verifyNoInteractions(studentRepository, requestRepository);
    }

    @Test
    void aStudentCannotSeeTheDetailOfARequestOfAnother() {
        Request ofAndres = new Request();
        ofAndres.setId(7);
        ofAndres.setStudentId(2);
        when(requestRepository.findById(7)).thenReturn(Optional.of(ofAndres));
        when(requestRequirementRepository.findByRequestId(7)).thenReturn(List.of());

        assertThrows(ForbiddenException.class, () -> requestService.getRequestById(7, LAURA));
    }
}
