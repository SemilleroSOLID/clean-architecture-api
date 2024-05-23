package com.example.demo.Infrastructure.Persistence.Repositories;

import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Interfaces.IConvocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ConvocationRepository implements IConvocationRepository {
    @Autowired

    @Override
    public List<Convocation> getAllConvocation() {
        return null;
    }
}
