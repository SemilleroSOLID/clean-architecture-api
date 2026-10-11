package com.example.demo.Domain.Interfaces;

import com.example.demo.Domain.Entities.RequestRequirement;

import java.util.List;

public interface IRequestRequirementRepository {
    List<RequestRequirement> saveAll(List<RequestRequirement> requestRequirements);
    List<RequestRequirement> findByRequestId(int requestId);
}
