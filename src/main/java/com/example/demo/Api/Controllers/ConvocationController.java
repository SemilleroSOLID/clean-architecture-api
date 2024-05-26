package com.example.demo.Api.Controllers;

import com.example.demo.Application.Dtos.CustomResponse;
import com.example.demo.Application.Services.ConvocationService;
import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Entities.ConvocationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("convocation")
public class ConvocationController {

    @Autowired
    private ConvocationService convocationService;

    @GetMapping("/getAllConvocationTypes")
    public CustomResponse<List<ConvocationType>> getAllConvocationTypes(){
        List<ConvocationType> response = convocationService.getAllConvocationTypes();
        return new CustomResponse<>(response, "Lista de tipos de convocatoria");
    }

    @PostMapping("/")
    public CustomResponse<Convocation> createConvocation(@RequestBody Convocation convocation ){
        Convocation createdconvocation = this.convocationService.createConvocation(convocation);
        return new CustomResponse<>(createdconvocation, "Nueva convocacion");
    }

}
