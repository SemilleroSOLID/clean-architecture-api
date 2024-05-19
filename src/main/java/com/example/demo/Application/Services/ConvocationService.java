package com.example.demo.Application.Services;

import com.example.demo.Domain.Entities.ConvocationType;
import com.example.demo.Domain.Interfaces.IConvocationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ConvocationService implements IConvocationService {
    @Autowired
    private IConvocationTypeRepository convocationTypeRepository;
    @Override
    public List<ConvocationType> getAllConvocationTypes() {
        return convocationTypeRepository.getAll();
    }
}
