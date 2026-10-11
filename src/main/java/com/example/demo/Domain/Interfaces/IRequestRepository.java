package com.example.demo.Domain.Interfaces;

import com.example.demo.Domain.Entities.Request;
import com.example.demo.Domain.Enums.EnumStateRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IRequestRepository {
    Request save(Request request);
    Optional<Request> findById(int requestId);
    List<Request> findAll();
    boolean existsByConvocationId(int convocationId);
    boolean existsByConvocationIdAndStateIn(int convocationId, Set<EnumStateRequest> states);
    boolean existsByStudentIdAndConvocationId(int studentId, int convocationId);
}
