package com.example.demo.Api.Controllers;

import com.example.demo.Application.Dtos.ConvocationDto;
import com.example.demo.Application.Dtos.CustomResponse;
import com.example.demo.Application.IRabbitMQSender;
import com.example.demo.Application.Services.ConvocationService;
import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Entities.ConvocationType;
import com.example.demo.Infrastructure.Implementations.Services.RabbitMQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("convocation")
public class ConvocationController {

    @Autowired
    private ConvocationService convocationService;

    @Autowired
    private IRabbitMQSender notificationSender;


    @GetMapping("/getAllConvocations")
    public CustomResponse<List<ConvocationDto>> getAllConvocations() {
        List<ConvocationDto> convocationsDto = this.convocationService.getAllConvocation();
        return new CustomResponse<>(convocationsDto, "All convocations list");
    }

    @GetMapping("/getConvocationById/{convocationId}")
    public CustomResponse<ConvocationDto> getConvocationById(@PathVariable("convocationId") Integer convocationId) {
        ConvocationDto convocationDto = this.convocationService.getConvocationById(convocationId);
        return new CustomResponse<>(convocationDto, "Convocation with id " + convocationDto.getId());
    }

    @PostMapping("/sendNotification")
    public CustomResponse<String> sendNotification(@RequestBody String message) {
        String response = this.notificationSender.sendMessage(message);
        return new CustomResponse<String>(response, "Mensaje enviado");
    }

    @GetMapping("/getAllConvocationTypes")
    public CustomResponse<List<ConvocationType>> getAllConvocationTypes() {
        List<ConvocationType> response = convocationService.getAllConvocationTypes();
        return new CustomResponse<>(response, "Lista de tipos de convocatoria");
    }

    @PostMapping("/createConvocation")
    public CustomResponse<ConvocationDto> createConvocation(@RequestBody ConvocationDto convocation) {
        ConvocationDto createdConvocation = this.convocationService.createConvocation(convocation);
        return new CustomResponse<>(createdConvocation, "New convocation");
    }

}
