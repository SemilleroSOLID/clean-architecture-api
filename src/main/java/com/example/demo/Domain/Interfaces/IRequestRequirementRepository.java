package com.example.demo.Domain.Interfaces;

import com.example.demo.Domain.Entities.RequestRequirement;

import java.util.Collection;
import java.util.List;

public interface IRequestRequirementRepository {
    List<RequestRequirement> saveAll(List<RequestRequirement> requestRequirements);
    List<RequestRequirement> findByRequestId(int requestId);

    /** The values of several requests in one query. */
    List<RequestRequirement> findByRequestIds(Collection<Integer> requestIds);
}
