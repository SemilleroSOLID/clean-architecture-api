package com.example.demo.Application;

import com.example.demo.Application.Dtos.RequestDetailDto;
import com.example.demo.Application.Dtos.RequestDto;
import com.example.demo.Application.Dtos.RequestSummaryDto;
import com.example.demo.Application.Dtos.StudentRequestDto;
import com.example.demo.Domain.Enums.EnumStateRequest;

import java.util.List;

public interface IRequestService {
    RequestDto createRequest(RequestDto request);

    /** All the requests, optionally only of a convocation and/or in a state. */
    List<RequestSummaryDto> getAllRequests(Integer convocationId, EnumStateRequest state);

    RequestDetailDto getRequestById(int requestId);

    /** History of a student: every request with the data of its convocation, newest first. */
    List<StudentRequestDto> getRequestsByStudent(int studentId);

    RequestDetailDto approveRequest(int requestId);

    RequestDetailDto rejectRequest(int requestId, String comment);

    /** The student withdraws one of his requests that is still in review. */
    StudentRequestDto cancelRequest(int requestId, int studentId);
}
