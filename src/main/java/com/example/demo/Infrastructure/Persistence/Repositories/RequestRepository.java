package com.example.demo.Infrastructure.Persistence.Repositories;

import com.example.demo.Domain.Enums.EnumStateRequest;
import com.example.demo.Domain.Interfaces.IRequestRepository;
import com.example.demo.Infrastructure.Persistence.Cruds.IRequestCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public class RequestRepository implements IRequestRepository {
    @Autowired
    private IRequestCrudRepository requestCrudRepository;

    @Override
    public boolean existsByConvocationId(int convocationId) {
        return this.requestCrudRepository.existsByConvocationId(convocationId);
    }

    @Override
    public boolean existsByConvocationIdAndStateIn(int convocationId, Set<EnumStateRequest> states) {
        return this.requestCrudRepository.existsByConvocationIdAndRequestStateIn(convocationId,
                states.stream().map(EnumStateRequest::getValue).toList());
    }
}
