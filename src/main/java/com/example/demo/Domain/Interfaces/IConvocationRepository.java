package com.example.demo.Domain.Interfaces;

import com.example.demo.Domain.Entities.Convocation;

import java.util.List;
import java.util.Optional;

public interface IConvocationRepository {
   Convocation save(Convocation convocation);
   List<Convocation> findAll();
   Optional<Convocation> findById(int convocationId);
}
