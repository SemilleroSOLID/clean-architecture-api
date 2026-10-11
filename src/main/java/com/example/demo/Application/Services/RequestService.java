package com.example.demo.Application.Services;

import com.example.demo.Application.Dtos.RequestDto;
import com.example.demo.Application.Exceptions.ConflictException;
import com.example.demo.Application.Exceptions.NotFoundException;
import com.example.demo.Application.IRequestService;
import com.example.demo.Application.Mappers.RequestMapper;
import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Entities.Request;
import com.example.demo.Domain.Entities.RequestRequirement;
import com.example.demo.Domain.Enums.EnumStateRequest;
import com.example.demo.Domain.Interfaces.IConvocationRepository;
import com.example.demo.Domain.Interfaces.IConvocationRequirementRepository;
import com.example.demo.Domain.Interfaces.IRequestRepository;
import com.example.demo.Domain.Interfaces.IRequestRequirementRepository;
import com.example.demo.Domain.Interfaces.IStudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
public class RequestService implements IRequestService {
    @Autowired
    private IRequestRepository requestRepository;
    @Autowired
    private IRequestRequirementRepository requestRequirementRepository;
    @Autowired
    private IConvocationRepository convocationRepository;
    @Autowired
    private IConvocationRequirementRepository convocationRequirementRepository;
    @Autowired
    private IStudentRepository studentRepository;
    @Autowired
    private RequestMapper requestMapper;
    @Autowired
    private Clock clock;

    @Override
    @Transactional
    public RequestDto createRequest(RequestDto requestDto) {
        LocalDate today = LocalDate.now(this.clock);
        if (!this.studentRepository.existsById(requestDto.getStudentId())) {
            throw new NotFoundException("No existe un estudiante con id " + requestDto.getStudentId());
        }
        Convocation convocation = this.convocationRepository.findById(requestDto.getConvocationId())
                .orElseThrow(() -> new NotFoundException("No existe una convocatoria con id " + requestDto.getConvocationId()));
        convocation.applyDateRules(today);
        convocation.ensureCanReceiveRequests(today);
        if (this.requestRepository.existsByStudentIdAndConvocationId(requestDto.getStudentId(), requestDto.getConvocationId())) {
            throw new ConflictException("El estudiante ya tiene una solicitud en esta convocatoria");
        }

        Request request = this.requestMapper.toDomain(requestDto);
        request.validateAgainst(this.convocationRequirementRepository.findConvocationRequirements(convocation.getId()));
        request.setCreationDate(today);
        // Until the automatic verification of #14 exists, every request waits for a decision.
        request.setState(EnumStateRequest.PENDING);

        Request createdRequest = this.requestRepository.save(request);
        List<RequestRequirement> requirements = request.getRequirements();
        requirements.forEach(requirement -> requirement.setRequestId(createdRequest.getId()));
        createdRequest.setRequirements(this.requestRequirementRepository.saveAll(requirements));
        return this.requestMapper.toDto(createdRequest);
    }
}
