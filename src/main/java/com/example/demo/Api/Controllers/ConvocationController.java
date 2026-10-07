package com.example.demo.Api.Controllers;

import com.example.demo.Application.Dtos.ConvocationDto;
import com.example.demo.Application.Dtos.ConvocationTypeDto;
import com.example.demo.Api.Dtos.CustomResponse;
import com.example.demo.Application.IConvocationService;
import com.example.demo.Application.IRabbitMQSender;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("convocation")
public class ConvocationController {

    @Autowired
    private IConvocationService convocationService;

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
    public CustomResponse<List<ConvocationTypeDto>> getAllConvocationTypes() {
        List<ConvocationTypeDto> response = convocationService.getAllConvocationTypes();
        return new CustomResponse<>(response, "Lista de tipos de convocatoria");
    }

    @PostMapping("/createConvocation")
    public CustomResponse<ConvocationDto> createConvocation(@Valid @RequestBody ConvocationDto convocation) {
        ConvocationDto createdConvocation = this.convocationService.createConvocation(convocation);
        return new CustomResponse<>(createdConvocation, "New convocation");
    }

    @PutMapping("/updateConvocation/{convocationId}")
    public CustomResponse<ConvocationDto> updateConvocation(@PathVariable("convocationId") Integer convocationId,
                                                            @Valid @RequestBody ConvocationDto convocation) {
        ConvocationDto updatedConvocation = this.convocationService.updateConvocation(convocationId, convocation);
        return new CustomResponse<>(updatedConvocation, "Convocatoria actualizada");
    }

}
