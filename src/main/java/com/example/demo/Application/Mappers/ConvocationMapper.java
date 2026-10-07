package com.example.demo.Application.Mappers;

import com.example.demo.Application.Dtos.ConvocationDto;
import com.example.demo.Application.Dtos.ConvocationRequirementDto;
import com.example.demo.Application.Dtos.ConvocationTypeDto;
import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Entities.ConvocationRequirement;
import com.example.demo.Domain.Entities.ConvocationType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

@Component
public class ConvocationMapper implements IConvocationMapper {

    @Override
    public ConvocationDto toDto(Convocation convocation) {
        ConvocationDto convocationDto = new ConvocationDto();
        convocationDto.setId(convocation.getId());
        convocationDto.setTitle(convocation.getTitle());
        convocationDto.setDescription(convocation.getDescription());
        convocationDto.setType(convocation.getType());
        convocationDto.setStartDate(toDate(convocation.getStartDate()));
        convocationDto.setEndDate(toDate(convocation.getEndDate()));
        convocationDto.setState(convocation.getState());
        convocationDto.setConvocationRequirements(convocation.getConvocationRequirements().stream().map(this::toDto).toList());
        return convocationDto;
    }

    @Override
    public Convocation toDomain(ConvocationDto convocationDto) {
        Convocation convocation = new Convocation();
        convocation.setId(convocationDto.getId());
        convocation.setTitle(convocationDto.getTitle());
        convocation.setDescription(convocationDto.getDescription());
        convocation.setType(convocationDto.getType());
        convocation.setStartDate(toLocalDate(convocationDto.getStartDate()));
        convocation.setEndDate(toLocalDate(convocationDto.getEndDate()));
        convocation.setState(convocationDto.getState());
        if (convocationDto.getConvocationRequirements() != null) {
            convocation.setConvocationRequirements(convocationDto.getConvocationRequirements().stream().map(this::toDomain).toList());
        }
        return convocation;
    }

    @Override
    public ConvocationRequirementDto toDto(ConvocationRequirement convocationRequirement) {
        ConvocationRequirementDto convocationRequirementDto = new ConvocationRequirementDto();
        convocationRequirementDto.setId(convocationRequirement.getId());
        convocationRequirementDto.setName(convocationRequirement.getName());
        convocationRequirementDto.setRequiredValue(convocationRequirement.getRequiredValue());
        convocationRequirementDto.setConditional(convocationRequirement.getConditional());
        convocationRequirementDto.setDescription(convocationRequirement.getDescription());
        convocationRequirementDto.setRequirementId(convocationRequirement.getRequirementId());
        convocationRequirementDto.setConvocationId(convocationRequirement.getConvocationId());
        return convocationRequirementDto;
    }

    @Override
    public ConvocationRequirement toDomain(ConvocationRequirementDto convocationRequirementDto) {
        return new ConvocationRequirement(
                convocationRequirementDto.getId(),
                convocationRequirementDto.getName(),
                convocationRequirementDto.getRequiredValue(),
                convocationRequirementDto.getConditional(),
                convocationRequirementDto.getDescription(),
                convocationRequirementDto.getRequirementId(),
                convocationRequirementDto.getConvocationId());
    }

    @Override
    public ConvocationTypeDto toDto(ConvocationType convocationType) {
        return new ConvocationTypeDto(convocationType.getId(), convocationType.getConvocationTypeName());
    }

    // java.sql.Date keeps the "yyyy-MM-dd" format the front already reads.
    private static Date toDate(LocalDate localDate) {
        return localDate == null ? null : java.sql.Date.valueOf(localDate);
    }

    // The JSON dates arrive in UTC, so the calendar day is taken in UTC.
    private static LocalDate toLocalDate(Date date) {
        if (date == null) return null;
        if (date instanceof java.sql.Date sqlDate) return sqlDate.toLocalDate();
        return date.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
    }
}
