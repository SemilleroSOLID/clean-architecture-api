package com.example.demo.Application;

import com.example.demo.Application.Dtos.ConvocationDto;
import com.example.demo.Application.Dtos.ConvocationTypeDto;

import java.util.List;

public interface IConvocationService {
   List<ConvocationDto> getAllConvocation();
   ConvocationDto getConvocationById(int convocationId);
   List<ConvocationTypeDto> getAllConvocationTypes();
   ConvocationDto createConvocation(ConvocationDto convocation);
}
