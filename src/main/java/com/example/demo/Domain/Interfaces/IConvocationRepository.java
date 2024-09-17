package com.example.demo.Domain.Interfaces;

import com.example.demo.Application.Dtos.ConvocationDto;
import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Entities.ConvocationRequirement;
import com.example.demo.Infrastructure.Persistence.Entities.ConvocationEntity;

import java.util.List;

public interface IConvocationRepository {
   List<Convocation> getAllConvocation();
   ConvocationEntity createConvocation(ConvocationEntity convocation);
   List<ConvocationEntity> getAllConvocations();
   ConvocationEntity getConvocationById(int convocationId);
}
