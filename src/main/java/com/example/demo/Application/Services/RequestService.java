package com.example.demo.Application.Services;

import com.example.demo.Application.Dtos.RequestDetailDto;
import com.example.demo.Application.Dtos.RequestDto;
import com.example.demo.Application.Dtos.RequestSummaryDto;
import com.example.demo.Application.Exceptions.ConflictException;
import com.example.demo.Application.Exceptions.NotFoundException;
import com.example.demo.Application.IRequestService;
import com.example.demo.Application.Mappers.RequestMapper;
import com.example.demo.Application.Mappers.StudentMapper;
import com.example.demo.Domain.Entities.Convocation;
import com.example.demo.Domain.Entities.ConvocationRequirement;
import com.example.demo.Domain.Entities.Request;
import com.example.demo.Domain.Entities.RequestRequirement;
import com.example.demo.Domain.Entities.Student;
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
import java.util.Map;
import java.util.stream.Collectors;

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
    private StudentMapper studentMapper;
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
        List<ConvocationRequirement> convocationRequirements = this.convocationRequirementRepository.findConvocationRequirements(convocation.getId());
        request.validateAgainst(convocationRequirements);
        request.setCreationDate(today);
        List<ConvocationRequirement> unmetRequirements = request.verifyRequirements(convocationRequirements);

        Request createdRequest = this.requestRepository.save(request);
        List<RequestRequirement> requirements = request.getRequirements();
        requirements.forEach(requirement -> requirement.setRequestId(createdRequest.getId()));
        createdRequest.setRequirements(this.requestRequirementRepository.saveAll(requirements));

        RequestDto createdRequestDto = this.requestMapper.toDto(createdRequest);
        createdRequestDto.setUnmetRequirements(this.requestMapper.toUnmetDtos(unmetRequirements, createdRequest));
        return createdRequestDto;
    }

    @Override
    public List<RequestSummaryDto> getAllRequests(Integer convocationId, EnumStateRequest state) {
        Map<Integer, String> studentNames = this.studentRepository.findAll().stream()
                .collect(Collectors.toMap(Student::getId, Student::getName));
        Map<Integer, String> convocationTitles = this.convocationRepository.findAll().stream()
                .collect(Collectors.toMap(Convocation::getId, Convocation::getTitle));
        return this.requestRepository.findAll(convocationId, state).stream()
                .map(request -> this.requestMapper.toSummaryDto(request,
                        studentNames.get(request.getStudentId()), convocationTitles.get(request.getConvocationId())))
                .toList();
    }

    @Override
    public RequestDetailDto getRequestById(int requestId) {
        Request request = this.requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("No existe una solicitud con id " + requestId));
        request.setRequirements(this.requestRequirementRepository.findByRequestId(requestId));
        Student student = this.studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new NotFoundException("No existe un estudiante con id " + request.getStudentId()));
        Convocation convocation = this.convocationRepository.findById(request.getConvocationId())
                .orElseThrow(() -> new NotFoundException("No existe una convocatoria con id " + request.getConvocationId()));
        convocation.applyDateRules(LocalDate.now(this.clock));

        List<ConvocationRequirement> convocationRequirements = this.convocationRequirementRepository.findConvocationRequirements(convocation.getId());
        return this.requestMapper.toDetailDto(request, this.studentMapper.toDto(student), convocation,
                convocationRequirements, request.unmetRequirements(convocationRequirements));
    }
}
