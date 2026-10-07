package com.example.demo.Domain.Interfaces;

import com.example.demo.Domain.Entities.Requirement;

import java.util.List;

public interface IRequirementRepository {
    List<Requirement> findAll();
}
