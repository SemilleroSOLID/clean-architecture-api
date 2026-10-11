package com.example.demo.Application;

import com.example.demo.Application.Dtos.ConvocationDto;
import com.example.demo.Application.Dtos.ConvocationTypeDto;

import java.time.LocalDate;
import java.util.List;

public interface IConvocationService {
   List<ConvocationDto> getAllConvocation();
   ConvocationDto getConvocationById(int convocationId);
   List<ConvocationTypeDto> getAllConvocationTypes();
   ConvocationDto createConvocation(ConvocationDto convocation);
   ConvocationDto updateConvocation(int convocationId, ConvocationDto convocation);
   void deleteConvocation(int convocationId);
   ConvocationDto closeConvocation(int convocationId);
   ConvocationDto reopenConvocation(int convocationId, LocalDate newEndDate);
   ConvocationDto finishConvocation(int convocationId);
   ConvocationDto cancelConvocation(int convocationId, String reason);
}
