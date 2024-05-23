package com.example.demo.Infrastructure.Persistence.Repositories;

import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Interfaces.IConvocationRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ConvocationRepository implements IConvocationRepository {
    @Autowired

    @Override
    public List<Convocation> getAllConvocation() {
        return null;
    }
}
