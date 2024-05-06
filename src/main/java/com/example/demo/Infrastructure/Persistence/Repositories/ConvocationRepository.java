package com.example.demo.Infrastructure.Persistence.Repositories;

import com.example.demo.Domain.Interfaces.IConvocationRepository;
import com.example.demo.Infrastructure.Persistence.Cruds.IConvocationCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class ConvocationRepository implements IConvocationRepository {
    @Autowired
    private IConvocationCrudRepository convocationCrudRepository;
}
