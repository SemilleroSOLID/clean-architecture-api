package com.example.demo.Application;

import com.example.demo.Application.Dtos.ConvocationDto;
import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Entities.ConvocationType;

import java.util.List;

public interface IConvocationService {
   List<ConvocationDto> getAllConvocation();
   ConvocationDto getConvocationById(int convocationId);
   List<ConvocationType> getAllConvocationTypes();
   ConvocationDto createConvocation(ConvocationDto convocation);
}
