package com.example.demo.Api.Controllers;

import com.example.demo.Application.Dtos.CancelConvocationDto;
import com.example.demo.Application.Dtos.ConvocationDto;
import com.example.demo.Application.Dtos.ConvocationTypeDto;
import com.example.demo.Application.Dtos.ReopenConvocationDto;
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

    @DeleteMapping("/deleteConvocation/{convocationId}")
    public CustomResponse<Object> deleteConvocation(@PathVariable("convocationId") Integer convocationId) {
        this.convocationService.deleteConvocation(convocationId);
        return new CustomResponse<>(null, "Convocatoria eliminada");
    }

    @PutMapping("/closeConvocation/{convocationId}")
    public CustomResponse<ConvocationDto> closeConvocation(@PathVariable("convocationId") Integer convocationId) {
        return new CustomResponse<>(this.convocationService.closeConvocation(convocationId), "Convocatoria cerrada");
    }

    @PutMapping("/reopenConvocation/{convocationId}")
    public CustomResponse<ConvocationDto> reopenConvocation(@PathVariable("convocationId") Integer convocationId,
                                                            @Valid @RequestBody ReopenConvocationDto reopen) {
        return new CustomResponse<>(this.convocationService.reopenConvocation(convocationId, reopen.getEndDate()), "Convocatoria reabierta");
    }

    @PutMapping("/finishConvocation/{convocationId}")
    public CustomResponse<ConvocationDto> finishConvocation(@PathVariable("convocationId") Integer convocationId) {
        return new CustomResponse<>(this.convocationService.finishConvocation(convocationId), "Convocatoria finalizada");
    }

    @PutMapping("/cancelConvocation/{convocationId}")
    public CustomResponse<ConvocationDto> cancelConvocation(@PathVariable("convocationId") Integer convocationId,
                                                            @Valid @RequestBody CancelConvocationDto cancel) {
        return new CustomResponse<>(this.convocationService.cancelConvocation(convocationId, cancel.getReason()), "Convocatoria cancelada");
    }

}
