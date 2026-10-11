package com.example.demo.Infrastructure.Persistence.Repositories;

import com.example.demo.Domain.Entities.Request;
import com.example.demo.Domain.Enums.EnumStateRequest;
import com.example.demo.Domain.Interfaces.IRequestRepository;
import com.example.demo.Infrastructure.Mappers.RequestPersistenceMapper;
import com.example.demo.Infrastructure.Persistence.Cruds.IRequestCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class RequestRepository implements IRequestRepository {
    @Autowired
    private IRequestCrudRepository requestCrudRepository;
    @Autowired
    private RequestPersistenceMapper requestPersistenceMapper;

    @Override
    public Request save(Request request) {
        return this.requestPersistenceMapper.toDomain(this.requestCrudRepository.save(this.requestPersistenceMapper.toEntity(request)));
    }

    @Override
    public Optional<Request> findById(int requestId) {
        return this.requestCrudRepository.findById(requestId).map(this.requestPersistenceMapper::toDomain);
    }

    @Override
    public List<Request> findAll() {
        List<Request> requests = new ArrayList<>();
        this.requestCrudRepository.findAll().forEach(requestEntity -> requests.add(this.requestPersistenceMapper.toDomain(requestEntity)));
        return requests;
    }

    @Override
    public boolean existsByConvocationId(int convocationId) {
        return this.requestCrudRepository.existsByConvocationId(convocationId);
    }

    @Override
    public boolean existsByConvocationIdAndStateIn(int convocationId, Set<EnumStateRequest> states) {
        return this.requestCrudRepository.existsByConvocationIdAndRequestStateIn(convocationId,
                states.stream().map(EnumStateRequest::getValue).toList());
    }

    @Override
    public boolean existsByStudentIdAndConvocationId(int studentId, int convocationId) {
        return this.requestCrudRepository.existsByStudentIdAndConvocationId(studentId, convocationId);
    }
}
