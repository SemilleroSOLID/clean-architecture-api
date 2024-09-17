package com.example.demo.Domain.Interfaces;

import com.example.demo.Domain.Entities.ConvocationType;

import java.util.List;

public interface IConvocationTypeRepository {
    List<ConvocationType> getAll();
}
