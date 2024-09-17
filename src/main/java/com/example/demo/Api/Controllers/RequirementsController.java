package com.example.demo.Api.Controllers;

import com.example.demo.Application.Dtos.ConditionRequirementDto;
import com.example.demo.Application.Dtos.ConvocationDto;
import com.example.demo.Application.Dtos.CustomResponse;
import com.example.demo.Application.Dtos.RequirementDto;
import com.example.demo.Application.Services.RequirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("requirements")
public class RequirementsController {
    @Autowired
    private RequirementService requirementService;

    @GetMapping("/getAllRequirements")
    public CustomResponse<List<RequirementDto>> getAllRequirements() {
        List<RequirementDto> requirementDto = this.requirementService.getAllRequirements();
        return new CustomResponse<>(requirementDto, "All requirements list");
    }

    @GetMapping("/getAllConditionRequirements")
    public CustomResponse<List<ConditionRequirementDto>> getAllConditionRequirements() {
        List<ConditionRequirementDto> conditionRequirementDto = this.requirementService.getAllConditionsRequirement();
        return new CustomResponse<>(conditionRequirementDto, "All condition requirements list");
    }
}
