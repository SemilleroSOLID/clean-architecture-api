package com.example.demo.Domain.Interfaces;

import com.example.demo.Domain.Enums.EnumStateRequest;

import java.util.Set;

public interface IRequestRepository {
    boolean existsByConvocationId(int convocationId);
    boolean existsByConvocationIdAndStateIn(int convocationId, Set<EnumStateRequest> states);
}
