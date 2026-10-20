package com.example.demo.Application.Services;

import com.example.demo.Application.Dtos.RequestDetailDto;
import com.example.demo.Application.Dtos.RequestDto;
import com.example.demo.Application.Dtos.RequestSummaryDto;
import com.example.demo.Application.Dtos.StudentRequestDto;
import com.example.demo.Application.Exceptions.ConflictException;
import com.example.demo.Application.Exceptions.ForbiddenException;
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
import java.util.HashMap;
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
        List<Request> requests = this.requestRepository.findAll(convocationId, state);

        // The values of every request in one query, and the requirements of each convocation only once.
        Map<Integer, List<RequestRequirement>> answersByRequest = this.requestRequirementRepository
                .findByRequestIds(requests.stream().map(Request::getId).toList()).stream()
                .collect(Collectors.groupingBy(RequestRequirement::getRequestId));
        Map<Integer, List<ConvocationRequirement>> requirementsByConvocation = new HashMap<>();

        return requests.stream()
                .map(request -> {
                    request.setRequirements(answersByRequest.getOrDefault(request.getId(), List.of()));
                    List<ConvocationRequirement> convocationRequirements = requirementsByConvocation.computeIfAbsent(
                            request.getConvocationId(), this.convocationRequirementRepository::findConvocationRequirements);
                    return this.requestMapper.toSummaryDto(request, studentNames.get(request.getStudentId()),
                            convocationTitles.get(request.getConvocationId()), request.unmetRequirements(convocationRequirements));
                })
                .toList();
    }

    @Override
    public RequestDetailDto getRequestById(int requestId) {
        Request request = this.findRequest(requestId);
        return this.toDetailDto(request, this.findConvocationOf(request));
    }

    @Override
    public List<StudentRequestDto> getRequestsByStudent(int studentId) {
        if (!this.studentRepository.existsById(studentId)) {
            throw new NotFoundException("No existe un estudiante con id " + studentId);
        }
        LocalDate today = LocalDate.now(this.clock);
        Map<Integer, Convocation> convocations = new HashMap<>();
        return this.requestRepository.findByStudentId(studentId).stream()
                .map(request -> this.requestMapper.toStudentRequestDto(request,
                        convocations.computeIfAbsent(request.getConvocationId(), id -> this.findConvocation(id, today))))
                .toList();
    }

    private Convocation findConvocation(int convocationId, LocalDate today) {
        Convocation convocation = this.convocationRepository.findById(convocationId)
                .orElseThrow(() -> new NotFoundException("No existe una convocatoria con id " + convocationId));
        convocation.applyDateRules(today);
        return convocation;
    }

    @Override
    @Transactional
    public RequestDetailDto approveRequest(int requestId) {
        Request request = this.findRequest(requestId);
        Convocation convocation = this.findConvocationOf(request);
        request.approve(convocation);
        return this.toDetailDto(this.saveDecision(request), convocation);
    }

    @Override
    @Transactional
    public RequestDetailDto rejectRequest(int requestId, String comment) {
        Request request = this.findRequest(requestId);
        Convocation convocation = this.findConvocationOf(request);
        request.reject(convocation, comment);
        return this.toDetailDto(this.saveDecision(request), convocation);
    }

    @Override
    @Transactional
    public StudentRequestDto cancelRequest(int requestId, int studentId) {
        Request request = this.findRequest(requestId);
        if (!request.belongsTo(studentId)) {
            throw new ForbiddenException("Solo el estudiante que hizo la solicitud puede retirarla");
        }
        request.cancel();
        return this.requestMapper.toStudentRequestDto(this.saveDecision(request), this.findConvocationOf(request));
    }

    // The notification to the student of each decision is published in #20.
    private Request saveDecision(Request request) {
        Request saved = this.requestRepository.save(request);
        saved.setRequirements(request.getRequirements());
        return saved;
    }

    private Request findRequest(int requestId) {
        Request request = this.requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("No existe una solicitud con id " + requestId));
        request.setRequirements(this.requestRequirementRepository.findByRequestId(requestId));
        return request;
    }

    /** The convocation of the request with its effective state: an open one whose end date passed counts as closed. */
    private Convocation findConvocationOf(Request request) {
        return this.findConvocation(request.getConvocationId(), LocalDate.now(this.clock));
    }

    private RequestDetailDto toDetailDto(Request request, Convocation convocation) {
        Student student = this.studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new NotFoundException("No existe un estudiante con id " + request.getStudentId()));
        List<ConvocationRequirement> convocationRequirements = this.convocationRequirementRepository.findConvocationRequirements(convocation.getId());
        return this.requestMapper.toDetailDto(request, this.studentMapper.toDto(student), convocation,
                convocationRequirements, request.unmetRequirements(convocationRequirements));
    }
}
