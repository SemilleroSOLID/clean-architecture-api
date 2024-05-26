package com.example.demo.Application;

import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Entities.ConvocationType;

import java.util.List;

public interface IConvocationService {
   List<Convocation> getAllConvocation();
   List<ConvocationType> getAllConvocationTypes();
   Convocation createConvocation(Convocation convocation);
}
